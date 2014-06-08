package at.metalab.m68k.soup.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import at.metalab.m68k.soup.OAuthHelper;
import at.metalab.m68k.soup.SoupClient;
import at.metalab.m68k.soup.SoupClientImpl;
import at.metalab.m68k.soup.resource.Group;

public class SoupGroupsExample {

	public static void main(String[] args) throws Exception {
		Properties soupApiProperties = OAuthHelper.loadApiProperties();
		Properties accessTokenProperties = OAuthHelper
				.loadAccessTokenProperties();

		SoupClient soup = new SoupClientImpl(soupApiProperties,
				accessTokenProperties, 1337);

		System.out.println("Groups the current user has already joined");
		for (Group group : soup.groupsJoined()) {
			System.out.println(group.getName() + ": " + group.getUrl());
		}

		System.out.println("Query for sandbox:");
		Group sandbox = null;
		for (Group group : soup.groupSearch("sandbox")) {
			System.out.println(group.getName() + ": " + group.getUrl() + " #"
					+ group.getId());
			sandbox = group;
			break;
		}

		soup.groupJoin(sandbox);
		System.out.println("You should have joined the sandbox group now");
		System.out.println("Press enter to leave it again...");
		new BufferedReader(new InputStreamReader(System.in)).readLine();

		soup.groupLeave(sandbox);
		System.out.println("You should have left the sandbox group now");
	}
}
