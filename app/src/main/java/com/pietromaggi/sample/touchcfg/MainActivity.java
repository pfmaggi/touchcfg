package com.pietromaggi.sample.touchcfg;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.StringReader;

public class MainActivity extends AppCompatActivity implements EMDKListener {
    // Assign the profile name used in EMDKConfig.xml
    private String profileName = "TouchModeProfile";

    // Declare a variable to store ProfileManager object
    private ProfileManager m_EmdkProfile = null;

    // Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    // Provides the error type for characteristic-error
    private String errorType = "";

    // Provides the parm name for parm-error
    private String parmName = "";

    // Provides error description
    private String errorDescription = "";

    // Provides error string with type/name + description
    private String errorString = "";

    private TextView m_statusTextView = null;
    private Spinner m_spinTouchMode = null;
    private Button m_btnSet = null;


    private String PFM_TAG = "PFM SAMPLE APP -->";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(PFM_TAG, "onCreate");
        m_spinTouchMode = (Spinner) findViewById(R.id.spinTouchMode);
        m_statusTextView = (TextView) findViewById(R.id.textViewStatus);
        m_btnSet = (Button) findViewById(R.id.btnSet);

        m_btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = m_spinTouchMode.getSelectedItemPosition();

                String[] modifyData = new String[1];
                modifyData[0]=
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                                +"<characteristic type=\"Profile\">"
                                +"<parm name=\"ProfileName\" value=\""+profileName+"\"/>"
                                +"<characteristic type=\"TouchMgr\">"
                                +"<parm name=\"TouchAction\" value=\""+((mode==0)?("Stylus and Finger"):("Glove and Finger"))+"\"/>"
                                +"</characteristic>"
                                +"</characteristic>";


                if (m_EmdkProfile == null) {
                    return;
                }

                new ProcessProfileTask().execute(modifyData[0]);

            }
        });

        // The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        // Check the return status of getEMDKManager
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            // EMDKManager object creation failed
            Toast.makeText(this, "Issues getting a reference to the EMDK Manager", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(PFM_TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(PFM_TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(PFM_TAG, "onResume");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(PFM_TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(PFM_TAG, "onDestroy");

        // Clean up the objects created by EMDK manager
        if (m_EmdkProfile != null)
            m_EmdkProfile = null;

        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
    }

    @Override
    public void onClosed() {
        Log.d(PFM_TAG, "onClosed");

        //This callback will be issued when the EMDK closes unexpectedly.
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }

        m_statusTextView.setText("Status: " + "EMDK closed unexpectedly! Please close and restart the application.");
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        Log.d(PFM_TAG, "onOpened");
        this.emdkManager = emdkManager;

        m_EmdkProfile = (ProfileManager)emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

        if (m_EmdkProfile != null) {
            m_btnSet.setEnabled(true);
            m_statusTextView.setText("Status: " + "EMDK initialized correctly. Ready to set profile.");
        }
    }

    // Method to parse the XML response using XML Pull Parser
    public void parseXML(XmlPullParser myParser) {
        int event;
        try {
            // Retrieve error details if parm-error/characteristic-error in the response XML
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:

                        if (name.equals("parm-error")) {
                            parmName = myParser.getAttributeValue(null, "name");
                            errorDescription = myParser.getAttributeValue(null, "desc");
                            errorString = " (Name: " + parmName + ", Error Description: " + errorDescription + ")";
                            return;
                        }
                        if (name.equals("characteristic-error")) {
                            errorType = myParser.getAttributeValue(null, "type");
                            errorDescription = myParser.getAttributeValue(null, "desc");
                            errorString = " (Type: " + errorType + ", Error Description: " + errorDescription + ")";
                            return;
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                event = myParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ProcessProfileTask extends AsyncTask<String, Void, EMDKResults> {

        @Override
        protected EMDKResults doInBackground(String... params) {

            //Call process profile to modify the profile of specified profile name
            EMDKResults results = m_EmdkProfile.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, params);

            return results;
        }

        @Override
        protected void onPostExecute(EMDKResults results) {

            super.onPostExecute(results);

            String resultString = "???";

            //Check the return status of processProfile
            if (results.statusCode == EMDKResults.STATUS_CODE.SUCCESS) {
                resultString = "Profile update success.";
            } else if(results.statusCode == EMDKResults.STATUS_CODE.CHECK_XML) {

                // Get XML response as a String
                String statusXMLResponse = results.getStatusString();

                try {
                    // Create instance of XML Pull Parser to parse the response
                    XmlPullParser parser = Xml.newPullParser();
                    // Provide the string response to the String Reader that reads
                    // for the parser
                    parser.setInput(new StringReader(statusXMLResponse));
                    // Call method to parse the response
                    parseXML(parser);

                    if ( TextUtils.isEmpty(parmName) && TextUtils.isEmpty(errorType) && TextUtils.isEmpty(errorDescription) ) {

                        resultString = "Profile update success.";
                    }
                    else {

                        resultString = "Profile update failed." + errorString;
                    }

                } catch (XmlPullParserException e) {
                    resultString =  e.getMessage();
                }
            } else {
                resultString = "Profile update failed: " + results.getStatusString();
            }

            m_statusTextView.setText(resultString);
        }
    }
}
