package com.example.dsl;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public final class DSLManager{

    private static class InnerSingleton{
        private static final DSLManager instance=new DSLManager();
    }
    public static DSLManager getInstance(){
        return InnerSingleton.instance;
    }

    //awake
    private DSLManager(){
        //exs= new ThreadPoolExecutor(0,20,10, TimeUnit.MINUTES,new SynchronousQueue<Runnable>());
        exs =Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t=new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        Server=new ServerConnect("13.124.33.18");
        try{
            Runtime.getRuntime().addShutdownHook(new Thread(()-> Close()));
        }catch (Exception e){
            Close();
            e.printStackTrace();
        }

    }
    private ExecutorService exs;
    private ServerConnect Server;
    enum DBIDS{
        TimeSchedule,
        Noti,
        SchoolSchedule
    }
    public interface NetListener {
        void Result(JSONArray Result);
    }
    private class ServerConnect implements AutoCloseable{
        //debuging
        private final String targetIP;
        private String BaseURL;
        private Context cont;


        public ServerConnect(String targetIP){
            this.targetIP=targetIP;
        }
        private HttpsURLConnection RightConnect(){
            try{
                URL url = new URL(BaseURL);
                return (HttpsURLConnection) url.openConnection();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        //Only Debuging use
        private HttpsURLConnection DangerConnect(){
            try {
                CertificateFactory cf= CertificateFactory.getInstance("X.509");
                InputStream cainput;
                cainput=cont.getResources().openRawResource(R.raw.onlydebugingcrt);
                Certificate ca=cf.generateCertificate(cainput);
                cainput.close();

                String KeyStoreType= KeyStore.getDefaultType();
                KeyStore keystore=KeyStore.getInstance(KeyStoreType);

                keystore.load(null,null);
                keystore.setCertificateEntry("ca",ca);


                String tmalgorithm= TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf=TrustManagerFactory.getInstance(tmalgorithm);
                tmf.init(keystore);


                SSLContext context=SSLContext.getInstance("TLS");
                context.init(null,tmf.getTrustManagers(),null);


                URL url = new URL(BaseURL);
                HttpsURLConnection urlConnection=(HttpsURLConnection)url.openConnection();
                //안전하지 않아도 연결할지 확인
                urlConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        if(hostname.equals(targetIP)){
                            return true;
                        }
                        return false;

                    }
                });
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                return urlConnection;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        private String ConnectWork(HttpsURLConnection urlConnection, JSONObject parameter){
            if(parameter!=null){
                RequestSend(urlConnection,parameter);
            }
            String Result="";

            try{
                if(urlConnection.getResponseCode()==200){

                    InputStream in=urlConnection.getInputStream();
                    Result=StreamRead(in);

                    in.close();
                }else{
                    Log.i("DSL",urlConnection.getResponseMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }
            return Result;
        }
        private String setParameter(JSONObject parameter) throws JSONException {
            StringBuffer sb=new StringBuffer();
            Iterator iterator= parameter.keys();
            String id;
            while(iterator.hasNext()){
                id=iterator.next().toString();
                sb.append(id);
                sb.append("=");
                sb.append(parameter.getString(id));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }
        private void RequestSend(HttpsURLConnection con,JSONObject parameter){
            try{
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept","Application/json");

                //noti my code for test
                con.setRequestProperty("type",parameter.getJSONObject("input_json").getString("type"));

                OutputStream out=new BufferedOutputStream(con.getOutputStream());
                String param=setParameter(parameter);
                out.write(param.getBytes(StandardCharsets.UTF_8));
                out.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private String StreamRead(InputStream in){
            StringBuilder s= new StringBuilder();
            int len;
            byte[] buffer=new byte[1024];
            while(true){
                try{
                    len = in.read(buffer,0,buffer.length);
                }catch (Exception e){
                    e.printStackTrace();
                    break;
                }
                if(len==-1){
                    break;
                }
                s.append(new String(buffer));
            }
            return s.toString();
        }
        //Only test. must delete
        private HttpsURLConnection Connect(){
            return DangerConnect();
        }
        private String getResult(String result) throws JSONException, SQLException {
            String responseString = new JSONArray(result).getJSONObject(0).getString("result");
            if(responseString.equals("200")){
                return "Success";
            }
            throw new SQLException(responseString);
        }
        public void sendRequest(Context context, JSONObject json,String API_URL, NetListener netListener){
            if(cont==null){
                cont=context.getApplicationContext();
            }
            BaseURL ="https://"+targetIP+API_URL;
            Runnable task = ()->{
                JSONArray result;
                try {
                    result=new JSONArray(ConnectWork(Connect(),json));
                    if(netListener!=null){
                        netListener.Result(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            exs.submit(task);
        }
        @Override
        public void close() {
            exs.shutdown();
            cont=null;
            try {
                if (!exs.awaitTermination(60, TimeUnit.SECONDS)) {
                    exs.shutdownNow();
                    if (!exs.awaitTermination(60, TimeUnit.SECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                exs.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    public void sendRequest(Context context, JSONObject json,String API_URL, NetListener netListener){
        Server.sendRequest(context,json,API_URL,netListener);
    }
    public void Close() {
        Server.close();
        Server=null;

    }

}
