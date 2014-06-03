**Table of Contents**

- [Overview](#overview)
	- [Contributions](#contributions)
	- [Restrictions](#restrictions)
- [API v1.1](#api-v11)
	- [General remarks](#general-remarks)
	- [Authentication](#authentication)
		- [3 legged OAuth flow](#3-legged-oauth-flow)
			- [OAuth endpoints](#oauth-endpoints)
			- [Register your application](#register-your-application)
		- [Base URL](#base-url)
		- [User details](#user-details)
			- [groups](#groups)
			- [blogs](#blogs)
		- [Resources](#resources)
			- [Error codes for resources](#error-codes-for-resources)
- [Types](#types)
	- [Example](#example)
- [Posts](#posts)
	- [General Remarks](#general-remarks)
		- [Return value](#return-value)
		- [HTTP Status codes](#http-status-codes)
	- [Regular (text post)](#regular-text-post)
	- [Links](#links)
	- [Quotes](#quotes)
	- [Images](#images)
	- [Video](#video)
	- [Files](#files)
	- [General remarks](#general-remarks-1)
	- [Reviews](#reviews)
	- [Events](#events)
- [Groups](#groups)
	- [search](#search)
	- [Available](#available)
	- [Membership](#membership)
	- [resource](#resource)
		- [Creating a group](#creating-a-group)
	- [Listing joined groups](#listing-joined-groups)
- [Changelog](#changelog)
	- [api v1.1](#api-v11)

# Overview

This document describes the experimental API v1.1 for
[Soup.io](http://soup.io). This document is work in progress and can't be
considered complete or exhaustive. If you have any question regarding this
document or the API, please contact [@developers](http://developers.soup.io) on Soup.


## Contributions

We welcome any contribution to this document or example clients. In order
to contribute please use the github issue tracker or directly send pull
requests

## Restrictions

We don't provide any guarantees about this API and before using it please
be advised that we can't guarantee backwards compatibility for future
versions.


# API v1.1

This version of the Soup.io API is designed mostly for posting and creating
content. Currently it can't be used to browse Soup, modify profile
settings or create groups. It can be used however to upload videos, music, images
or post reviews.


## General remarks

* Accessing a resource which does not exist **or** the user does not have
  access to will result in a http `404` error.
* If you provide JSON,  please don't forget to set the `Content-type` to
  `application/json`.
* Uploading files requires to use form-encoded data. As form encoded data
  is **not** required to be signed all file uploads at the moment are non signed.
* **All** of the described API methods are rate limited and may return HTTP
  status code `403` at any time.


## Authentication

The Soup.io API is using a standard 3-legged OAuth 1.0A authentication to
provide access to user profiles.

### 3 legged OAuth flow

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
explicitly denied access to her user profile. This behavior is different to
other standard OAuth implementations.

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
requests have to be send via SSL.

### User details

You can obtain user details for the currently used `access token` and
`access secret` by doing an OAuth signed GET request to `/authenticate`,
e.g. `https://api.soup.io/api/v1.1/authenticate`.

By accessing `/authenticate` you can ensure that the used access tokens are
still valid. If they are no longer valid, HTTP status code 401 (401 Unauthorized)
is returned.

An example output for the `/authenticate` is provided below. The most
significant elements is the array `blogs`, consisting of all blogs,
respective groups the user has access to.

The following properties are part of a user:
* `blogs` - list of blogs the user has access to
* `groups` - list of resources for managing groups

The following is a complete example of user details:

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
    "groups": {
       "membership": "https://api.soup.io/api/v1.1/groups/user/",
       "resource": "https://api.soup.io/api/v1.1/groups/",
       "available": "https://api.soup.io/api/v1.1/groups/available",
       "search": "https://api.soup.io/api/v1.1/groups/user/search"
    },
    "name": "mrud",
    "resource": "https://api.soup.io/api/v1.1/users/168364"
  }
}
```


#### groups

The following code is an example about the group interface for a specific
user. This is returned as part of the authentication.

```json
"groups": {
 "membership": "https://api.soup.io/api/v1.1/groups/user/",
 "resource": "https://api.soup.io/api/v1.1/groups/",
 "available": "https://api.soup.io/api/v1.1/groups/available",
 "search": "https://api.soup.io/api/v1.1/groups/user/search"
}
```

Description:
* [`membership`](#membership) - manage the membership of a post,
  you can use `PUT` and `DELETE` to manage it. See
  [`membership`](#membership) for more details.
* [`resource`](#group-resource) - create  groups for the specific
  user, you can use `POST` to manage your own groups.
* [`available`](#available) - check the availability of the group name
* [`search`](#search) - used to search for groups which can be joined


#### blogs

The following code is an example of a blog list with one blog element. This
is returned as part of the authentication.

```json
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
    },
    ...
 }
]

```

Description:
* [`resource`](#resource) - is the base url for accessing or creating posts on the
  specific blog
* [`tags`](#tags) returns a list of tags used on the specific blog. This can be used
  to provide a completion when choosing the appropriate tag for a post
* [`types`](#types) returns a list of resources for specific post types.
* `permissions`
  * `admin` - administrator of this group, can post and remove members
  * `approved` - part of the group, can post to this group
  * `owner` - home blog, typically the users own blog


General remarks
---
* The properties described for each post type should be inside a `post`
  dictionary. To encode the properties `a`, `b`, and `c`:
  * If you have to encode the data as file use the following field names:
    * `post[a]`
    * `post[b]`
    * `post[c]`
  * For json make sure that the properties are inside a post dictionary, e.g.:

```json
        {
          "post": {
            "a": ...,
            "b": ...,
            "c", ...
          }
        }
```


### Resources
A resource in the Soup.io API provides additional information for described
element. The URI is `absolute` and must be used to get access.

#### Error codes for resources

Accessing resources which do not exist or the user does not have access to
will result in a `http 404` error code.


# Types

Soup.io currently supports different posts type, such as `text`, `links`,
`videos`, etc. Accessing the type resource will provide an array with
possible types. The number of types can change in future implementation.
For API v1.1 the following types do exist and can be identified via the
`name` attribute:

* [`regular`](#regular-text-post) equals to the text post on soup
* [`links`](#links) equals to the link post on soup
* [`quotes`](#quotes) equals to the quote post on soup
* [`images`](#images) equals to the image post on soup
* [`videos`](#videos) equals to the video post on soup
* [`files`](#files) equals to the file post on soup
* [`reviews`](#reviews) equals to the review post on soup
* [`events`](#events) equals to the event post on soup
* [`audio`](#files) equals to the file post on soup, uploading an audio
file

## Example

By accessing  `https://api.soup.io/api/v1.1/blogs/168342/types`, extracted from the
[`User`](#user-details) above, we can get the URI for a specific post type.

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

For example, the URI to create a [`regular`](#regular-text-posts) post
is `https://api.soup.io/api/v1.1/blogs/168342/posts/regular`.

# Posts

## General Remarks
* To create a post, send an HTTP `POST` requests to the url specified in
  [types](#types), e.g. to post a image send an HTTP POST request to
  `https://api.soup.io/api/v1.1/blogs/168342/posts/images`.
* the return value is a json dict, containing an `id`, representing the post
  and some other, currently unspecified values.
* all posts can also be tagged. This is done by setting the `tags` field.
  Tags are either separated by white-space or comma. A mixture of boths is
  not supported.
* All fields in general are optional. If no field is specified an empty
  post is created.

### Return value

All successful posts generate a json dictionary with information about the
created post. To see if the post was created successfully check the `id`
property of the post.

```json
{ "post": {
    "updated_at": "2014-05-07T05:41:15.318Z",
    "created_at":"2014-05-07T05:41:15.320Z",
     "id":155,
     "blog_id":11,
     ....
  }
}
```
### HTTP Status codes

The API typically returns the following http status codes:
* `201` - object created, aka post was created successfully
* `401` - oauth signature does not verify, check time, do not reuse nonce
  etc.
* `404` - either the resource does not exist or the user does not have
  access to it


## Regular (text post)

Following properties are supported for a regular post:
* `title` - the title used for the post
* `body` - body of the post
* `source` - source where the content was acquired from

Example:
```json
{ "post": { "body": "lorem ipsulum", "title": "Great content", "tags": "old" } }
```

## Links
Following properties are supported for a link post:
* `url` - the url of the link, aka source
* `description` - body of the post, i.e. the description of the link
* `caption` - title of the post

```json
{
  "post": {
     "url": "https://soup.io",
     "caption": "Your own space in the net",
     "description": "Soup is an awesome place where you can do much....",
     "tags": "first tag, second tag, 3rd tag"
   }
}
```
## Quotes
Following properties are supported for the quote post:
* `quote` - the quote itself
* `source` - source of the quote

Example:
```json
{ "post": { "source": "awesome book", "quote": "lorem ipsulum" } }
```

## Images
Following properties are supported for a image post:
* `url` - url of an image to download
* `description` - body of the post
* `source` - source of the image
* `file` - upload file, make sure to use form-encoded data to upload the
  file

### JSON Example
```json
{ "post":  {
   "url": "http://imgs.xkcd.com/comics/coupon_code.png",
   "description": "Another XKCD comic",
   "source": "http://xkcd.com",
   "tags": "xkcd comic"
  }
}

```
### File upload example

This is an exempt how to upload an image with python

```python
params = { "post[source]": 'http://xkcd.com',
           "post[description]": 'Another XKCD Comic',
           "post[tags]": "xkcd comic"
}

r = requests.Request('POST', uri, auth=oauth_auth)
prepared = r.prepare()
auth_header = {'Authorization': prepared.headers.get('Authorization')}

requests.post(uri,
              files={'file': open('coupon_code.png')},
              data=params, headers=auth_header)
```


## Video
Following properties are supported for a video post:
* `url`- url or embed code which should be used to display the video
* `caption` - caption/body of the post  **deprecated**
* `embed-code` - embed code or url which should be used to display the
  video
* `description` - caption/body of the post
* `file` - file to upload

Either `url`/`embed-code` or `file` should be present before posting. The
API currently does not require any parameter to be set (similar to the Soup
behavior) but you should set at least one of these parameters.

### JSON Example

```json
{
  "post": {
    "url": "http://www.youtube.com/watch?v=uq83lU6nuS8",
    "description": "A direct video link (this is the caption of the video)",
    "tags": "youtube, video, example"
  }
}
```

### File upload example

This is an exempt how to post with python

```python
params = { "post[source]": 'my mobile phone!',
           "post[description]": 'An uploaded video',
           "post[tags]": "embarrassing fun soup"
}

r = requests.Request('POST', uri, auth=oauth_auth)
prepared = r.prepare()
auth_header = {'Authorization': prepared.headers.get('Authorization')}

requests.post(uri,
              files={'file': open('video.mp4')},
              data=params, headers=auth_header)
```


## Files
Following properties are supported for a file post:
* `url`- url of a file to download (currently not working)
* `caption` - caption/body of the post **deprecated**
* `description` - caption/body of the post
* `file` - file to upload

Either `url` or `file` should be set.


### JSON Example

```json
{
  "post": {
    "url": "http://www.cs.uiuc.edu/homes/snir/PPP/models/gotoharmful.pdf",
    "description": "A must read why goto is considered harmful"
  }
}
```

### File upload

```python
params = { "post[description]": 'A must read'
}

r = requests.Request('POST', uri, auth=oauth_auth)
prepared = r.prepare()
auth_header = {'Authorization': prepared.headers.get('Authorization')}

requests.post(uri,
              files={'file': open('gotoharmful.pdf')},
              data=params, headers=auth_header)

```
## Reviews
Following properties are supported for a file post:
* `title`- title of the review
* `review` - description of the review
* `rating` - number of stars from zero to five
* `url` - should point to what we reviewed
* `file` - file to upload (TODO)

rating should be an integer from zero to five, representing the number of
stars for the given review.
If url is set we try to download and display it, if the content is not
suitable, we ignore it at the moment.

Currently the behavior of the `file` property is unspecified.

To rate the amazing paper from before we can use following example:

```json
{
  "post": {
    "url": "http://www.cs.uiuc.edu/homes/snir/PPP/models/gotoharmful.pdf",
    "review": "A must read",
    "rating": 5,
    "tags": "opinion rating pdf compsci"
  }
}
```

## Events
Following properties are supported for an event post:
* `title` - title of the event
* `location` - textual description of the location
* `description` - description of the event
* `start_date` - start date in
  [rfc2822 format](http://www.w3.org/Protocols/rfc822/#z28)
* `end_date` - end date in [rfc2822 format](http://www.w3.org/Protocols/rfc822/#z28)
* `url` - currently undefined behavior
* `file` - currently undefined behavior

```json
  "post": {
    "title": "Soup.io meetup",
    "location": "Vienna",
    "description": "Come by and get red",
    "start_date": "Tue, 01 Apr 2014 22:00:42 +0200",
    "end_date": "Tue, 01 Apr 2014 22:42:00 +0200",
    "tags": "attend hashtagrl"
  }
}
```

# Groups

You can manage your group membership via the group resources provided in
the user response. The following is the response from the user information.


```json
"groups": {
 "membership": "https://api.soup.io/api/v1.1/groups/user/",
 "resource": "https://api.soup.io/api/v1.1/groups/",
 "available": "https://api.soup.io/api/v1.1/groups/available",
 "search": "https://api.soup.io/api/v1.1/groups/user/search"
}
```

```
| HTTP Verb | Url                  | Action                           |
|-----------+----------------------+----------------------------------|
| GET       | /groups/search?q=    | search for groups                |
| GET       | /groups/available?q= | check if group name is available |
| PUT       | /groups/user/:id     | join group                       |
| DELETE    | /groups/user/:id     | leave group                      |
| POST      | /groups/             | create a new group               |
| GET       | /groups/user         | list of joined groups            |
```

## search

`GET https://api.soup.io/api/v1.1/groups/user/search`

### Parameters
* `q` **(required)** - UTF-8 encoded search string

### Return value

An array containing matched groups. An empty array is returned if no matching group
could be found.

For the example `GET` request to
`https://api.soup.io/api/v1.1/groups/user/search?q=test` we may get
something like

```json
{
  "groups": [
    {
      "id": 13,
      "image_url": null,
      "name": "testerlein",
      "title": "testerlein's soup",
       "url": "http://testerlein.soup.io"
    },
    {
      "id": 14,
      "image_url": null,
      "name": "test2group",
      "title": "test2group's soup",
      "url": "http://test2group.soup.io"
    }
  ]
}

```

If no match can be found an empty array is returned:

```json
{ "groups": [] }
```

## Available

The idea behind this endpoint is to check in advance if a given name is
still available.

`GET https://api.soup.io/api/v1.1./groups/available`

### Parameters
* `q` **(required)** - UTF-8 encoded group name

### Return value

* HTTP `200` is returned if the name is free
* HTTP `400` is returned if `q` is missing
* HTTP `412` is returned if the name is already taken

If the name is free we return a json object indicating the availability:

`GET https://api.soup.io/api/v1.1/groups/available?q=the-best-name-evar`

```json
{  "status": "free" }
```

If the name is already taken, `status` is set to `used`.

## Membership

* To join a specific group you have to do send a `PUT` request, e.g.
  `PUT https://api.soup.io/api/v1.1/groups/user/:ID`, this will
  add you to the specific group.
* To leave a specific group you have to send a `DELETE` request, e.g
  `DELETE https://api.soup.io/api/v1.1/groups/user/:ID`, this
  will leave the specific group.

For both request type, HTTP status `202` is returned if the join/leave of
the group was successful.

To get a `ID` please use the search function.

### Return value

* An HTTP error code `404` is returned if the group with the `ID` does not
  exist
* The group details are returned as a json dict if the join/leave was
  successful, e.g:

Example requests:

`PUT https://api.soup.io/api/v1.1/groups/user/13` - will join the group 13
and return the details of the group.

`DELETE https://api.soup.io/api/v1.1/groups/user/13` - will leave the group
13 and return the details of the group.

```json
{
  "group":  {
      "id": 13,
      "image_url": null,
      "name": "testerlein",
      "title": "testerlein's soup",
       "url": "http://testerlein.soup.io"
    }
}
```
## resource

The resource in the group property allows you to manage your groups.
To create a new group send a HTTP `POST` request. For groups where you are
admin you can modify them (HTTP `PUT` and append the id)  them.


### Parameters
* `name` **(required)** - the name of the group
* `privacy` **(required)** - either `open`, `approval`, or `invite`.
* `title`  (optional) - titel of the group
* `image_url` (optional) - url of the avatar to be used on the web site

If either name or privacy are missing HTTP status code `400` is returned,
if the login does already exists or the username doesn't validate HTTP
status code `412` is returned.


Example:

```json
{
  "group": {
     "name": "soup-over-tea",
     "privacy": "approval",
  }
}
```

### Creating a group
`POST https://api.soup.io/api/v1.1/groups` with the example above
will create a group called `soup-over-tea` where members have to be
approved. The current user will be automatically group admin. The returned
data is the details of the group:

```json
{
  "group":  {
      "id": 14,
      "image_url": null,
      "name": "soup-over-tea",
      "title": "soup-over-tea's soup",
       "url": "http://soup-over-tea.soup.io"
    }
}
```
HTTP Return codes:
* `202` if the group was created
* `400` if either `name` or `privacy` are missing
* `412` if the name is already taken, name is invalid or privacy is invalid

HTTP Return codes:
* `200` if the group was deleted successfully
* `404` if either the group does not exist or the user does not have admin privileges

## Listing joined groups


`GET https://api.soup.io/api/v1.1/groups/user/`

### Parameters
* none, all are ignored

### Return value

An array containing joined groups. An empty array is returned if no matching group
could be found.

For the example `GET` request to
`https://api.soup.io/api/v1.1/groups/user/search` we may get
something like

```json
{
  "groups": [
    {
      "id": 13,
      "image_url": null,
      "name": "testerlein",
      "title": "testerlein's soup",
       "url": "http://testerlein.soup.io"
    },
    {
      "id": 14,
      "image_url": null,
      "name": "test2group",
      "title": "test2group's soup",
      "url": "http://test2group.soup.io"
    }
  ]
}

```

If no match can be found an empty array is returned:

```json
{ "groups": [] }
```
# Changelog

## api v1.1

* First public document
* Changed api endpoints to https://api.soup.io/api/
* Changed oauth endpoints to api subdomain.
* Added documentation about file uploads
* Added missing post types
* Added http status codes
* Added group documentation
* Adjust group list url
* Add documentation for listing group membership
