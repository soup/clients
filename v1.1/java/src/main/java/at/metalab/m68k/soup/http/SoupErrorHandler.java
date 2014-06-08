package at.metalab.m68k.soup.http;

import org.scribe.model.Request;
import org.scribe.model.Response;

import at.metalab.m68k.soup.http.exceptions.BadRequestException;
import at.metalab.m68k.soup.http.exceptions.ForbiddenException;
import at.metalab.m68k.soup.http.exceptions.InternalServerErrorException;
import at.metalab.m68k.soup.http.exceptions.NotFoundException;
import at.metalab.m68k.soup.http.exceptions.PreconditionFailedException;
import at.metalab.m68k.soup.http.exceptions.UnauthorizedException;

public class SoupErrorHandler {

	public void handle(Request request, Response response) {
		switch (response.getCode()) {
		case 400:
			on400();
			break;
		case 401:
			on401();
			break;
		case 403:
			on403();
			break;
		case 404:
			on404();
			break;
		case 412:
			on412();
			break;
		case 500:
			on500();
			break;
		}
	}

	protected void on400() {
		throw new BadRequestException();
	}

	protected void on401() {
		throw new UnauthorizedException();
	}

	protected void on403() {
		throw new ForbiddenException();
	}

	protected void on404() {
		throw new NotFoundException();
	}

	protected void on412() {
		throw new PreconditionFailedException();
	}

	protected void on500() {
		throw new InternalServerErrorException();
	}
}
