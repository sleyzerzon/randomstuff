
package com.randomhumans.svnindex.indexing;

import java.util.SortedSet;
import java.util.TreeSet;

import org.tmatesoft.svn.core.SVNDirEntry;

public class DefaultNameAndSizeFilter implements IFilter
{
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
        .getLog(DefaultNameAndSizeFilter.class);
    
    SortedSet<String> nameFilters = null;

    static final long KB = 1024;

    static final long MB = DefaultNameAndSizeFilter.KB * DefaultNameAndSizeFilter.KB;

    public DefaultNameAndSizeFilter(final String[] filters)
    {
        this.nameFilters = new TreeSet<String>();
        for (final String s : filters)
        {
            this.nameFilters.add(s);
        }        
    }

    public boolean allow(final String url, final SVNDirEntry entry)
    {        
        boolean process =  false;
        try
        {
        process = !this.nameFilters.contains(entry.getName())
            && !entry.getAuthor().equalsIgnoreCase("nextgenbuilder")
            && (entry.getSize() < 2 * DefaultNameAndSizeFilter.MB);
        } catch (NullPointerException npe)
        {
            log.warn(npe);
        }
        if (process)
        {
            DirectoryEntryThreadPool.queueEntry(url, entry);
        }
        return process;
    }
}
