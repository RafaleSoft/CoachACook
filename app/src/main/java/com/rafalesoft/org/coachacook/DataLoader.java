package com.rafalesoft.org.coachacook;

import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class DataLoader implements ContentHandler
{
    public boolean load_data(String xmlSource)
    {
        try
        {
            InputStream input = new FileInputStream(xmlSource);
            Xml.parse(input, Xml.Encoding.ISO_8859_1, this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (SAXException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public abstract void onElementLoaded(String localName, Attributes attrs);

    @Override
    public void characters(char[] ch, int start, int length)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void endDocument()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void endPrefixMapping(String prefix)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void processingInstruction(String target, String data)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // TODO Auto-generated method stub

    }

    @Override
    public void skippedEntity(String name)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void startDocument()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs)
    {
        onElementLoaded(localName, attrs);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
    {
        // TODO Auto-generated method stub
    }
}
