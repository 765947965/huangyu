package com.android.internal.telephony;
import java.util.List;
//这个声明是为了能调用系统挂断电话的功能
interface ITelephony {
	  void dial(String paramString);

    void call(String paramString);

    boolean showCallScreen();
    

   boolean showCallScreenWithDialpad(boolean paramBoolean);
    

   boolean endCall();
    

   void answerRingingCall();
    

   void silenceRinger();
    

   boolean isOffhook();
    

   boolean isRinging();
    

   boolean isIdle();
    

   boolean isRadioOn();
    

   boolean isSimPinEnabled();
    

   void cancelMissedCallsNotification();
    

   boolean supplyPin(String paramString);
    

   boolean handlePinMmi(String paramString);
    

   void toggleRadioOnOff();
    

   boolean setRadio(boolean paramBoolean);
    

   void updateServiceLocation();
    

   void enableLocationUpdates();
    

   void disableLocationUpdates();
    

   int enableApnType(String paramString);
    

   int disableApnType(String paramString);
    

   boolean enableDataConnectivity();
    

   boolean disableDataConnectivity();
    

   boolean isDataConnectivityPossible();
    

   Bundle getCellLocation();
    

    

   int getCallState();
    

   int getDataActivity();
    

   int getDataState();
    

   int getActivePhoneType();
    

   int getCdmaEriIconIndex();
    

   int getCdmaEriIconMode();
    

   String getCdmaEriText();
    

   boolean getCdmaNeedsProvisioning();

   int getVoiceMessageCount();

   int getNetworkType();
    

   boolean hasIccCard();
    
}