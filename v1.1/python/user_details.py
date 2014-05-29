#!/usr/bin/env python
# -*- coding: utf8 -*-
__author__ = 'uli'

import urlparse
import sys
import simplejson as json
import logging
import oauth2
import yaml
import os
logging.basicConfig() 
logging.getLogger().setLevel(logging.DEBUG)



class AccessTokenMissing(Exception):
    pass


class PermissionDenied(Exception):
    pass


class AccessToken(object):
    """Proxy class handling the acquisition and caching of access tokens"""

    def __init__(self, filename, url_prefix, consumer_token, consumer_secret, callback):

        self.filename = filename
        self.url_prefix = url_prefix
        self.consumer_token = consumer_token
        self.consumer_secret = consumer_secret
        self.request_token_url = "{0}/oauth/request_token".format(url_prefix)
        self.access_token_url = "{0}/oauth/access_token".format(url_prefix)
        self.authorize_url = "{0}/oauth/authorize".format(url_prefix)
        self.callback = callback
        self.oauth_verifier = None

    def on_load(self, frame, request, action, *args):
        """ used to extract the oauth verifier from the callback
        """
        url = action.get_uri()

        if self.callback in url:
            self.win.hide()

            if "error" in url:
                raise PermissionDenied

            self.oauth_verifier = url.split('oauth_verifier=')[-1]
            self.win.destroy()

            return True

        return False

    def verifier(self, url):
        """Get the verifier, should be a factory to instanciate the correct view, e.g pyqt or gtk or ask"""

        try:
            import gtk
            import webkit
        except ImportError:
            import sys

            logging.error("Please install python-webkit")
            sys.exit(1)

        self.view = webkit.WebView()

        sw = gtk.ScrolledWindow()
        sw.add(self.view)

        self.win = gtk.Window(gtk.WINDOW_TOPLEVEL)
        self.win.set_geometry_hints(min_width=840, min_height=640)
        self.win.set_position(gtk.WIN_POS_CENTER)
        self.win.connect("destroy", lambda x: gtk.main_quit())
        self.win.add(sw)

        self.view.connect("navigation-policy-decision-requested", self.on_load)

        self.view.open(url)
        self.win.show_all()
        gtk.main()

        return self.oauth_verifier

    def tokens(self):

        consumer = oauth2.Consumer(self.consumer_token, self.consumer_secret)
        client = oauth2.Client(consumer)

        # Request request tokens
        resp, content = client.request(self.request_token_url, "GET")

        if resp['status'] != '200':
            raise Exception("Invalid response %s." % resp['status'])

        request_token = dict(urlparse.parse_qsl(content))

        logging.debug("Request Token:\n" +
                      "    - oauth_token        = {0[oauth_token]}\n".format(request_token) +
                      "    - oauth_token_secret = {0[oauth_token_secret]}\n".format(request_token))

        # interactively ask for permission/connect to the account
        verifier = self.verifier("%s?oauth_token=%s" % (self.authorize_url, request_token['oauth_token']))

        token = oauth2.Token(request_token['oauth_token'],
                             request_token['oauth_token_secret'])
        token.set_verifier(verifier)

        # convert request token + verifier token into access token
        client = oauth2.Client(consumer, token)
        resp, content = client.request(self.access_token_url, "POST")

        access_token = dict(urlparse.parse_qsl(content))
        return access_token

    def get(self):
        """Returns either cached data or ask the user to login into her account"""

        access_token = None
        access_secret = None

        # use the cache or otherwise get the tokens via the user
        if os.path.exists(self.filename):
            with open(self.filename, "r") as f:
                saved = yaml.load(f) or {}
                access = saved.get(self.url_prefix, {})

                access_token = access.get("oauth_token", "")
                access_secret = access.get("oauth_token_secret", "")

        if not access_token or not access_secret:
            tokens = self.tokens()
            access_token = tokens.get("oauth_token", "")
            access_secret = tokens.get("oauth_token_secret", "")

        return access_token, access_secret


def update_file(data, filename):
    """Update the token file with updated credentials"""
    saved = {}
    if os.path.exists(filename):
        with open(filename, "r") as f:
            saved = yaml.load(f) or {}

    merged = dict(saved.items() + data.items())

    with open(filename, "w") as f:
        yaml.dump(merged, f, default_flow_style=False)


def get_details(consumer_token, consumer_secret, credentials_file="soup_tokens.yaml", url_prefix='https://api.soup.io/',
                callback="http://soup-login/"):
    """Return details about the connected user"""

    token_proxy = AccessToken(credentials_file, url_prefix, consumer_token, consumer_secret, callback)
    access_key, access_secret = token_proxy.get()

    update_file({url_prefix: {"oauth_token": access_key, "oauth_token_secret": access_secret}}, credentials_file)

    consumer = oauth2.Consumer(consumer_token, consumer_secret)
    access_token = oauth2.Token(key=access_key, secret=access_secret)
    client = oauth2.Client(consumer, access_token)



    # Get user details
    user_details_url = "{0}/api/v1.1/authenticate".format(url_prefix)

    # we need to explicityl accept application/json, otherwise
    # we won't get a result
    response, data = client.request(user_details_url, headers={'accept': 'application/json'})

    if response.status == 401:
        # Either user revoked the access token, consumer tokes were revoked or time is off
        logging.error("Could not authenticate?!")
        sys.exit(1)

    if response.status != 200:
        logging.error("Something went wrong, HTTP return code is {0}".format(response.status))
        sys.exit(1)
    return json.loads(data)


def main():
    import argparse

    parser = argparse.ArgumentParser(description='Example client for soup.io.')
    parser.add_argument('--config', dest='config', action='store',
                        default='soup.yaml',
                        help='The config file to use')

    parser.add_argument('--url-prefix', dest='url_prefix', action='store',
                        default="https://api.soup.io",
                        help='URL prefix to use')

    parser.add_argument('--credentials', dest='credentials', action='store',
                        default="soup_tokens.yaml",
                        help='File to save soup tokens')

    args = parser.parse_args()
    config_file = {}

    if os.path.exists(args.config):
        with open(args.config, "r") as f:
            config_file = yaml.load(f)
    else:
        logging.warning("Could not load {0}, create config or adjust parameters".format(args.config))

    consumer_token = config_file.get("token", "")
    consumer_secret = config_file.get("secret", "")
    callback = config_file.get("callback", "http://soup-login/")

    if not consumer_token or not consumer_secret:
        logging.error("Could not load secret or token, register application at https://soup.io/oauth_clients/")
        sys.exit(1)

    details = get_details(consumer_token, consumer_secret, url_prefix=args.url_prefix, callback=callback)
    print json.dumps(details, indent=4, sort_keys=True)


if __name__ == '__main__':
    main()
