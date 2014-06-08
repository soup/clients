package at.metalab.m68k.soup.resource.posts;

import at.metalab.m68k.soup.http.SoupRequest;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

public interface Postable {

	SoupRequest<PostResult> createRequest(Blog blog);

}
