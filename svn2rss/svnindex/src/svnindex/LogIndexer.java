package svnindex;

import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

public class LogIndexer
{
	static long	BATCH_SIZE	= 400;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws SVNException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException
	{
		SVNRepository repository = RepositoryFactory.getRepo();
		long endRev = repository.getLatestRevision();
		IndexWriter iw = new IndexWriter("z:/index", new StandardAnalyzer(), true);
		try
		{
			for (int i = 1; i < endRev; i += BATCH_SIZE + 1)
			{
				Collection<SVNLogEntry> logs = repository.log(new String[]
				{ "" }, null, i, Math.min(i + BATCH_SIZE, endRev), true, true);
				for (SVNLogEntry entry : logs)
				{
					iw.addDocument(RevisionDocument.createRevisionDocument(entry));
					System.out.println(Long.toString(entry.getRevision()));
				}
			}
		} finally
		{
			iw.optimize();
			iw.close();
		}
	}

}
