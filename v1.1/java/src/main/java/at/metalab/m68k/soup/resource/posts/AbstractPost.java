package at.metalab.m68k.soup.resource.posts;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import at.metalab.m68k.soup.http.SoupRequest;
import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.http.requests.JsonRequestImpl;
import at.metalab.m68k.soup.http.requests.MultipartRequestImpl;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

public abstract class AbstractPost extends PostProperties implements Postable {

	protected abstract SoupRequestBuilder<PostResult> createPost(Blog blog);

	private final static ObjectMapper OM = new ObjectMapper();

	private static PostResult unmarshalJsonRepsonse(Response response)
			throws JsonProcessingException, IOException {
		JsonNode rootNode = OM.readTree(response.getBody());
		JsonNode postNode = rootNode.get("post");

		return PostResult.create(postNode);
	}

	protected abstract class JsonPostTemplate extends
			JsonRequestImpl<PostResult> implements
			SoupRequestBuilder<PostResult> {

		protected abstract void buildPostNode(ObjectNode postNode)
				throws IOException, JsonMappingException, JsonParseException;

		private String url;

		public JsonPostTemplate(Blog blog, String endpoint) {
			this.url = String.format("%s%s", blog.getResource(), endpoint);
		}

		@Override
		protected at.metalab.m68k.soup.http.requests.JsonRequestImpl.JsonInput createJsonInput() {
			try {
				ObjectNode rootNode = OM.createObjectNode();

				ObjectNode postNode = OM.createObjectNode();
				rootNode.put("post", postNode);

				buildPostNode(postNode);

				String body = OM.writerWithDefaultPrettyPrinter()
						.writeValueAsString(rootNode);

				return new JsonInput(Verb.POST, url, body);
			} catch (IOException ioException) {
				throw new RuntimeException("", ioException);
			}
		}

		@Override
		protected PostResult unmarshalJson(Response response)
				throws JsonProcessingException, IOException {
			return unmarshalJsonRepsonse(response);
		}

		public SoupRequest<PostResult> create() {
			return new JsonRequestImpl<PostResult>() {
				@Override
				protected at.metalab.m68k.soup.http.requests.JsonRequestImpl.JsonInput createJsonInput() {
					return JsonPostTemplate.this.createJsonInput();
				}

				@Override
				protected PostResult unmarshalJson(Response response)
						throws JsonProcessingException, IOException {
					return JsonPostTemplate.this.unmarshalJson(response);
				}
			};
		}
	}

	protected abstract class MultipartPostTemplate extends
			MultipartRequestImpl<PostResult> implements
			SoupRequestBuilder<PostResult> {

		protected abstract String getFilename();

		protected abstract String getTags();

		protected abstract String getDescription();

		protected abstract InputStream getData();

		private String url;

		public MultipartPostTemplate(Blog blog, String endpoint) {
			super(Verb.POST);

			url = String.format("%s%s", blog.getResource(), endpoint);
		}

		@Override
		protected HttpEntity buildEntity() {
			return MultipartEntityBuilder
					.create()
					.addPart("post[file]",
							new InputStreamBody(getData(), getFilename()))
					.addPart("post[tags]",
							new StringBody(getTags(), ContentType.TEXT_PLAIN))
					.addPart(
							"post[description]",
							new StringBody(getDescription(),
									ContentType.TEXT_PLAIN)).build();
		}

		@Override
		protected PostResult unmarshalJson(Response response)
				throws JsonProcessingException, IOException {
			return unmarshalJsonRepsonse(response);
		}

		public SoupRequest<PostResult> create() {
			return new MultipartRequestImpl<PostResult>(Verb.POST, url) {
				protected HttpEntity buildEntity() {
					return MultipartPostTemplate.this.buildEntity();
				}

				@Override
				protected PostResult unmarshalJson(Response response)
						throws JsonProcessingException, IOException {
					return MultipartPostTemplate.this.unmarshalJson(response);
				}
			};
		}
	}

	protected static String formatRfc822(Calendar calendar) {
		return new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US)
				.format(calendar.getTime());
	}

	public SoupRequest<PostResult> createRequest(Blog blog) {
		return createPost(blog).create();
	}
}
