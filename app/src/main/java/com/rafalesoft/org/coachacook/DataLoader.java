package com.rafalesoft.org.coachacook;

import android.content.Context;
import android.database.SQLException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.RequiresApi;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import org.apache.commons.net.ftp.FTPFile;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

abstract class DataLoader implements ContentHandler
{
    static RecipesDB _db = null;
    static byte[] ftpaddress = new byte[]{(byte) 192, (byte) 168, 1, 23};
    static String ftphostname = "mathqcm.ddns.net";

    public static void setDatabase(RecipesDB db) { _db = db; }

    public static boolean downloadFile(String filename)
    {
        try
        {
            FTPClient ftpclient = new FTPClient();
            InetAddress address = InetAddress.getByAddress(ftpaddress);
            ftpclient.connect(ftphostname, 21);
            if (ftpclient.isConnected() && ftpclient.login("coachacook", "coachacook"))
            {
                ftpclient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpclient.enterLocalPassiveMode();

                FTPFile[] files = ftpclient.listFiles("/");
                boolean fileExist = false;
                for (FTPFile f : files)
                {
                    if (f.getName() == filename)
                        fileExist = true;
                }

                if (!fileExist)
                    return false;

                OutputStream output = new FileOutputStream(CoachACook.getCoach().getFilesDir() + File.separator + filename);
                if (ftpclient.retrieveFile("/" + filename, output))
                {
                    output.close();
                    Log.d("DataLoader", "downloaded file " + filename);
                }

                ftpclient.logout();
                ftpclient.disconnect();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean load_data(String filename)
    {
        if (filename.isEmpty())
            return true;

        try
        {
            InputStream input = null;

            String xmlSource = findFilePath(CoachACook.getCoach(), filename);
            if (!xmlSource.isEmpty())
            {
                Log.d("DataLoader", "parsing " + xmlSource);
                input = new FileInputStream(xmlSource);
            }
            else
                return false;

            Xml.parse(input, Xml.Encoding.ISO_8859_1, this);
            input.close();
        }
        catch (IOException | SAXException | SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String findFilePath(Context ctx, String filename)
    {
        String xmlPath = File.separator + filename;

        File dataDir = new File(ctx.getExternalFilesDir(null) + xmlPath);
        if (!dataDir.exists())
        {
            dataDir = new File(Environment.getDataDirectory() + xmlPath);
            if (!dataDir.exists())
            {
                dataDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + xmlPath);
                if (!dataDir.exists())
                {
                    dataDir = new File(ctx.getFilesDir() + xmlPath);
                    if (!dataDir.exists())
                    {
                        dataDir = new File(ctx.getDataDir().getPath() + xmlPath);
                        if (!dataDir.exists())
                            return "";
                    }
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
