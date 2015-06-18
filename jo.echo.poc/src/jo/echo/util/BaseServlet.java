package jo.echo.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazon.speech.Sdk;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class BaseServlet extends SpeechletServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;

    static
    {
        System.setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, "true");
        System.setProperty(Sdk.SUPPORTED_APPLICATION_IDS_SYSTEM_PROPERTY, "");
        System.setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, "");
    }
    
    private Throwable mLastException = null;
    protected String mIntentsFile;
    protected String mUtterancesFile;
    
    public BaseServlet()
    {
    }
    
    @Override
    protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException
    {
        try
        {
            super.doPost(arg0, arg1);
        }
        catch (ServletException e1)
        {
            mLastException = e1;
            throw e1;
        }
        catch (IOException e2)
        {
            mLastException = e2;
            throw e2;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        String fetch = req.getParameter("fetch");
        resp.setContentType("text/plain");
        if ("intents".equals(fetch))
            doGetResource(req, resp, mIntentsFile);
        else if ("utterances".equals(fetch))
            doGetResource(req, resp, mUtterancesFile);
        else if (mLastException != null)
        {
            Throwable t = mLastException;
            while (t != null)
            {
                resp.getWriter().append(t.getLocalizedMessage()+":\r\n");
                for (StackTraceElement ste : t.getStackTrace())
                    resp.getWriter().append("  "+ste.toString()+":\r\n");
                if (t instanceof Exception)
                    t = ((Exception)t).getCause();
                else
                    t = null;
            }
        }        
        else
            resp.getWriter().append("Hello from the "+getClass().getSimpleName()+" app.");
    }

    protected void doGetResource(HttpServletRequest req, HttpServletResponse resp, String file)
            throws ServletException, IOException
    {
        if (file == null)
            return;
        String path = getClass().getPackage().getName().replace('.', '/');
        String resource = path + "/slu/"+file;
        InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
        for (;;)
        {
            int ch = is.read();
            if (ch < 0)
                break;
            resp.getOutputStream().write(ch);
        }
        is.close();
    }
}