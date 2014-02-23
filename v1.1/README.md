**Table of Contents**

- [Overview](#overview)
  - [Contributions](#contributions)
  - [Restrictions](#restrictions)
  - [Table of Contents](#table-of-contents)
- [API v1.1](#api-v11)
  - [Authentication](#authentication)
  - [Base URL](#base-url)
  - [User details](#user-details)
  - [Types](#types)
  - [Posts](#posts)
- [Changelog](#changelog)

# Overview

This document describes the experimental API v1.1 for
[Soup.io](http://soup.io). This document is work in progress and can't be
considered complete or exhaustive. If you have any question regarding this
document or the API, please contact [@developers](http://developers.soup.io) on Soup.


### Contributions

We welcome any contribution to this document or example clients. In order
to contribute please use the github issue tracker or directly send pull
requests

### Restrictions

We don't provide any guarantees about this API and before using it please
be advised that we can't guarantee backwards compatibility for future
versions.


### Table of Contents

# API v1.1

This version of the Soup.io API is designed mostly for posting and creating
content. Currently it can't be used to browse Soup, modify profile
settings or create groups. It can be used however to upload videos, music, images
or post reviews.


### Authentication

The Soup.io API is using a standard 3-legged OAuth 1.0A authentication to
provide access to user profiles.

#### 3 legged OAuth flow

The auth flow is basically the same as with Twitter or other OAuth
instances.

1. You have to request a token and secret from the
request_token endpoint. This will be used to link your aplication with the
user.

2. Redirect the user to the authorize url, and use the
previously acquired token from step one, e.g. for oauth1:
`https://api.soup.io/oauth/authorize?oauth_token=TOKEN_STEP_1`
<br />If the user allowed access to her account an redirect will be issued to
the specified callback. Parameters for this requests are `oauth_token` with
the used token and `oauth_verifier`. <br />
If the user denies the access soup.io redirects to the callback uri and
sets the `error` parameter  to `access_denied`. This means the user
explicitly denied access to her user profile. This behavior is different

3. The oauth token and oauth verifier have to replaced by an
`access_token`. This `access_token` has to be used to identify an user in
the future. This can be achieved by accessing the `access_token` endpoint,
e.g `https://api.soup.io/oauth/access_token`



#### OAuth endpoints

The Soup OAuth endpoints are:
* **request token** https://api.soup.io/oauth/request_token
* **access token** https://api.soup.io/oauth/access_token
* **authorize** https://api.soup.io/oauth/authorize

#### Register your application

The behavior and URLs described in this section will change in the future.
You will have to be a member of the [@developers](http://developers.soup.io)
group in order to register your application. After we implemented this
behavior, the consumer keys from users who are not in the group will be
deleted.

You can register your application and obtain consumer key and secret by
visiting
[https://api.soup.io/ouath_clients/](http://api.soup.io/oauth_clients/).
This URL provides also an overview about application given access to your
account. These settings will be integrated into your normal profile
version. The above URLs should be considered temporally and may change
without further notice.


### Base URL

The base URL for the Soup API v1.1 is `https://api.soup.io/api/v1.1/`. All
requests have to send via SSL.

### User details

You can obtain user details for the currently used `access token` and
`access secret` by doing an OAuth signed GET request to `/authenticate`,
e.g. `https://api.soup.io/api/v1.1/authenticate`.

By accessing `/authenticate` you can ensure that the used access tokens are
still valid. If they are no longer HTTP status code 401 (401 Unauthorized)
is returned.

An example output for the `/authenticate` is provided below. The most
significant elements is the array `blogs`, consisting of all blogs,
respective groups the user has access to.

* `resoure` - is the base url for accessing or creating posts on the
  specific blog
* [`tags`](#tags) returns a list of tags used on the specific blog. This can be used
  to provide a completion when choosing the appropriate tag for a post
* [`types`](#types) returns a list of resources for specific post types.
* `permissions`
  * `approved` - part of the group, can post to this group
  * `ownewr` - home blog, typically the users own blog

```json
{
  "user": {
    "blogs": [
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-4.soup.io/asset/4694/0703_458c_48.png",
          "name": "mrud",
          "permissions": ["owner"],
          "resource": "https://api.soup.io/api/v1.1/blogs/168342",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/168342/tags" },
          "title": "mr. u's soup",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/168342/types" },
          "url": "http://mrud.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-b.soup.io/asset/0260/7532_bcec_48.jpeg",
          "name": "gifluv",
          "permissions": ["approved"],
          "resource": "https://api.soup.io/api/v1.1/blogs/123206",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/123206/tags" },
          "title": "fuck yeah animated gifs",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/123206/types" },
          "url": "http://gifluv.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-7.soup.io/asset/0180/1923_792d_48.jpeg",
          "name": "urbanart",
          "permissions": ["approved"],
          "resource": "https://api.soup.io/api/v1.1/blogs/74607",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/74607/tags" },
          "title": "T h e   U r b a n  S o u p",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/74607/types" },
          "url": "http://urbanart.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-1.soup.io/asset/0005/0469_1a71_48.jpeg",
          "name": "fyi",
          "permissions": ["approved"],
          "resource": "https://api.soup.io/api/v1.1/blogs/4431",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/4431/tags" },
          "title": "For your information:",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/4431/types" },
          "url": "http://fyi.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-8.soup.io/asset/0101/9758_83c4_48.png",
          "name": "typography",
          "permissions": ["approved"],
          "resource": "https://api.soup.io/api/v1.1/blogs/36042",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/36042/tags" },
          "title": "typography soup",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/36042/types" },
          "url": "http://typography.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": null,
          "name": "uliistderbeste",
          "permissions": ["admin"],
          "resource": "https://api.soup.io/api/v1.1/blogs/2226031",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/2226031/tags" },
          "title": "uliistderbeste's soup",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/2226031/types" },
          "url": "http://uliistderbeste.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": null,
          "name": "muhahahahah",
          "permissions": ["admin"],
          "resource": "https://api.soup.io/api/v1.1/blogs/2226039",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/2226039/tags" },
          "title": "muhahahahah's soup",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/2226039/types" },
          "url": "http://muhahahahah.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-7.soup.io/asset/0104/1786_7d96_48.png",
          "name": "faq",
          "permissions": ["approved"],
          "resource": "https://api.soup.io/api/v1.1/blogs/44549",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/44549/tags" },
          "title": "Soup FAQ",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/44549/types" },
          "url": "http://faq.soup.io"
        }
      },
      {
        "blog": {
          "features": { "upload_limit": 10485760 },
          "image_url": "http://asset-0.soup.io/asset/0176/1454_04ec_48.png",
          "name": "comments",
          "permissions": ["approved"],
          "resource": "https://api.soup.io/api/v1.1/blogs/70007",
          "tags": { "resource": "https://api.soup.io/api/v1.1/blogs/70007/tags" },
          "title": "soup comments workaround",
          "types": { "resource": "https://api.soup.io/api/v1.1/blogs/70007/types" },
          "url": "http://comments.soup.io"
        }
      }
    ],
    "name": "mrud",
    "resource": "https://api.soup.io/api/v1.1/users/168364"
  }
}
```


### Types

You have to use the `type` resource to get a list of possible posts you can
create. `resource` specifies the url for the given resource and `name` the
descriptive name for it, e.g. `images`, `videos`, `files` etc.


```json
{
  "types": [
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/events", "name": "events" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/images", "name": "images" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/links", "name": "links" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/quotes", "name": "quotes" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/regular", "name": "regular" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/reviews", "name": "reviews" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/videos", "name": "videos" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/files", "name": "files" },
    { "resource": "https://api.soup.io/api/v1.1/blogs/168342/posts/audio", "name": "audio" }
  ]
}
```
### Posts

To create a post, send an HTTP `POST` requests to the url specified in
[tags](#tags), e.g. to post a image send an HTTP POST request to
`https://api.soup.io/api/v1.1/blogs/168342/posts/images`.

When posting the content please don't forget to set the `Content-type` to
`application/json`.

#### Regular (text post)

Following properties are supported for a regular post:
* `title` - the title used for the post (optional)
* `body` - body of the post
* `source` - source where the content was acquired from (optional)

Example:
```json
{ "post": { "body": "lorem ipsulum", "title": "Great content" } }
```

#### Video
Following properties are supported for a video post:
* `url`- the url which should be included
* `caption` - caption/body of the post (optional)

Example:
```json
{
  "post": {
    "url": "http://www.youtube.com/watch?v=uq83lU6nuS8",
    "caption": "A direkt video link (this is the caption of the video)"
  }
}
```

### Quotes
Following properties are supported for the quote post:
* `quote` - the quote itself
* `source` - source of the quote (optional)

Example:
```json
{ "post": { "source": "awesome book", "quote": "lorem ipsulum" } }
```




# Changelog

### api v1.1

* First public document
* Changed api endpoints to https://api.soup.io/api/
* Changed oauth endpoints to api subdomain.
