package com.eveningoutpost.dexdrip.webservices;

import android.util.Log;

import com.eveningoutpost.dexdrip.Models.ActiveBgAlert;
import com.eveningoutpost.dexdrip.Models.AlertType;
import com.eveningoutpost.dexdrip.Models.UserError;
import com.eveningoutpost.dexdrip.Models.UserNotification;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.eveningoutpost.dexdrip.Models.JoH.tolerantParseDouble;

/**
 * Created by bbassett on 08/25/2019.
 */

public class WebServiceAlerts extends BaseWebService {

    private static String TAG = "WebServiceAlerts";

    // process the request and produce a response object
    public WebResponse request(String query) {
        final JSONArray reply = new JSONArray();

        // populate json structures
        try {

            ActiveBgAlert activeBgAlert = ActiveBgAlert.getOnly();
            if (activeBgAlert != null) {
                AlertType alertType = AlertType.get_alert(activeBgAlert.alert_uuid);
                if (alertType != null) {

                    final JSONObject alert = new JSONObject();
                    alert.put("name", alertType.name);
                    alert.put("is_snoozed", activeBgAlert.is_snoozed);
                    reply.put(alert);
                }
            }
            final List<String> alert_types = Arrays.asList(
                    "bg_alert", "calibration_alert", "double_calibration_alert",
                    "extra_calibration_alert", "bg_unclear_readings_alert",
                    "bg_missed_alerts", "bg_rise_alert", "bg_fall_alert",
                    "bg_predict_alert");
            long now = new Date().getTime();
            for(String alertType : alert_types) {
                UserNotification userNotification = UserNotification.GetNotificationByType(alertType);
                if (userNotification != null) {
                    final JSONObject alert = new JSONObject();
                    alert.put("name", alertType);
                    //alert.put("started_at", activeAlert.alert_started_at);
                    alert.put("is_snoozed", userNotification.timestamp > now);
                    reply.put(alert);
                }
            }

            Log.d(TAG, "Output: " + reply.toString());
        } catch (JSONException e) {
            UserError.Log.wtf(TAG, "Got json exception: " + e);
        }

        return new WebResponse(reply.toString());
    }


}
