package com.rafalesoft.org.coachacook;

import android.content.Context;
import android.database.SQLException;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

abstract class DataLoader implements ContentHandler
{
    static RecipesDB _db = null;

    public static void setCook() { _db = CoachACook.getCoach().getRecipesDB(); }

    public boolean load_data(String filename)
    {
        if (filename.isEmpty())
            return true;

        try
        {
            String xmlSource = findFilePath(CoachACook.getCoach(), filename);
            if (!xmlSource.isEmpty())
            {
                Log.d("DataLoader", "parsing " + xmlSource);
                InputStream input = new FileInputStream(xmlSource);
                Xml.parse(input, Xml.Encoding.ISO_8859_1, this);
            }
            else
                return false;
        }
        catch (IOException | SAXException | SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String findFilePath(Context ctx, String filename)
    {
        String xmlPath = File.separator + filename;

        File dataDir = new File(ctx.getDataDir().getPath() + xmlPath);
        if (!dataDir.exists())
        {
            dataDir = new File(ctx.getExternalFilesDir(null) + xmlPath);
            if (!dataDir.exists())
            {
                dataDir = new File(Environment.getDataDirectory() + xmlPath);
                if (!dataDir.exists())
                {
                    dataDir = new File(ctx.getFilesDir() + xmlPath);
                    if (!dataDir.exists())
                        return "";
                }
            }
        }

        return dataDir.getPath();
    }

    protected abstract void onElementLoaded(String localName, Attributes attrs);

    @Override
    public void characters(char[] ch, int start, int length)
    {
        Log.d("DataLoader", "void stub");
    }

    @Override
    public void endDocument()
    {
        Log.d("DataLoader", "void stub endDocument");
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        Log.d("DataLoader", "void stub endElement");
    }

    @Override
    public void endPrefixMapping(String prefix)
    {
        Log.d("DataLoader", "void stub");
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
    {
        Log.d("DataLoader", "void stub");
    }

    @Override
    public void processingInstruction(String target, String data)
    {
        Log.d("DataLoader", "void stub");
    }

    @Override
    public void setDocumentLocator(Locator locator)
    {
        Log.d("DataLoader", "void stub");
    }

    @Override
    public void skippedEntity(String name)
    {
        Log.d("DataLoader", "void stub");
    }

    @Override
    public void startDocument()
    {
        Log.d("DataLoader", "void stub startDocument");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs)
    {
        onElementLoaded(localName, attrs);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
    {
        Log.d("DataLoader", "void stub");
    }
}
