
package com.randomhumans.rss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import viecili.jrss.generator.RSSFeedGenerator;
import viecili.jrss.generator.RSSFeedGeneratorFactory;
import viecili.jrss.generator.elem.Category;
import viecili.jrss.generator.elem.Channel;
import viecili.jrss.generator.elem.Item;
import viecili.jrss.generator.elem.RSS;

public class SVNLogRssGenerator {

	public static void main(String[] args) {

		System.out.println(buildRSS(getLogs()));
	}
	
	static String buildRSS(Collection<SVNLogEntry> logs)
	{
		String result = null;
		RSS rss = new RSS();
		Channel channel = new Channel("Logs", "link", "description");
		for(SVNLogEntry log: logs)
		{
			Item i = logEntryToRssItem(log);
			channel.addItem(i);
		}
		rss.addChannel(channel);
		RSSFeedGenerator gen = RSSFeedGeneratorFactory.getDefault();
		try {
			result = gen.generateAsString(rss);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static Item logEntryToRssItem(SVNLogEntry log) {
		String author = log.getAuthor() + "";
		String title = Long.toString(log.getRevision()) +":" + log.getMessage();
		StringBuffer sb = new StringBuffer();
		/*
		for(String s : log.getChangedPaths().g)
		{
			sb.append(s);
			sb.append(";\r\n");
		}
		String body = sb.toString();*/
		String body = "";
		Item i = new Item(title, "link", body);
		i.setAuthor(author);
		return i;
	}

	
	@SuppressWarnings("unchecked")
	static Collection<SVNLogEntry> getLogs()
	{
		Collection<SVNLogEntry> logs = new ArrayList<SVNLogEntry>();
		String url = "http://randomstuff.googlecode.com/svn";
		long startRev = 0;
		long endRev = -1;
		setupSVNLibrary();		
		SVNRepository repo = null;
		try {
			repo = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
		} catch (SVNException e) {
			e.printStackTrace();
		}
		try {
			endRev = repo.getLatestRevision();
			System.out.println(endRev);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		try {
			logs.addAll(repo.log(new String[] {""}, null, startRev, endRev, true, true));
		} catch (SVNException e) {
			e.printStackTrace();
		}		
		return logs;		
	}


	private static void setupSVNLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
	}
}
