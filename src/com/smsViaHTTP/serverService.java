package com.smsViaHTTP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Contacts;

public class serverService extends Service{

	public static boolean is_changed = false;
	  
	static String new_content = "<messages>";
	
	private ServerSocket serverSocket;
	
	public static Thread sT;
	
	private Handler handler = new Handler();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;		
	}

	@Override
	 public void onCreate() {
	  super.onCreate();
	  sT = new Thread(new serverThread());
	  sT.start();
	 }

	@Override
	 public void onDestroy() {
	  super.onDestroy();
	  try {
		serverSocket.close();
	  } catch (Exception  e) {
	  }
	  sT.stop();
	  handler.post(new Runnable() {
			public void run() {
				smsViaHTTPActivity.serverStatus.setText("Server stopped.");
			}
		});
	 }
	
	public class serverThread implements Runnable {

		private int serverPort = 8080;
		
		public void run() {
			handler.post(new Runnable() {
				public void run() {
					smsViaHTTPActivity.serverStatus.setText("Listening on IP: " + getLocalIpAddress() + ":" + serverPort);
				}
			});
			
			try {
				serverSocket = new ServerSocket(serverPort);
				
				while(true) {
					Socket client = serverSocket.accept();	
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						DataOutputStream out = new DataOutputStream(client.getOutputStream());
						http_handler(in, out);
						out.close();
						in.close();
						client.shutdownInput();
						client.shutdownOutput();
						client.close();
						break;
					} catch (Exception e) {
					}
				}	
			} catch (Exception e) {	
			}
		}
		
		private void http_handler(BufferedReader input, DataOutputStream output) {
			String path = new String();

			try {
				String tmp = input.readLine();
				String tmp2 = new String(tmp);
				
				tmp.toUpperCase();
				if (tmp.startsWith("GET")) { 
					int start = 0;
				    int end = 0;
				      
				    for (int a = 0; a < tmp2.length(); a++) {
				    	if (tmp2.charAt(a) == ' ' && start != 0) {
				    		end = a;
					        break;
				    	}
					    if (tmp2.charAt(a) == ' ' && start == 0) {
					    	start = a;
					 	}
				      }
				    
				      path = tmp2.substring(start + 2, end);
				      
				      if (path.equals("")) { 
				    	  path = "index.html";
				      }
				      
				      int type_is = 0;
				      
			    	  if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
					  	  type_is = 1;
					  } else if (path.endsWith(".png")) {
						  type_is = 2;
					  } else if (path.endsWith(".html") || path.endsWith(".htm")) {
						  type_is = 3;
					  } else if (path.endsWith(".js")) {
						  type_is = 4;
					  } else if (path.endsWith(".act")) {
						  type_is = 5;
					  } else if (path.endsWith(".css")) {
						  type_is = 6;
					  } else if (path.endsWith(".gif")) {
						  type_is = 8;
					  }
			    	  
			    	  if (type_is == 5) {
			    		  if (path.equals("is_changed.act")) {
		    				  output.writeBytes(construct_http_header(200, 5));
			    			  if (is_changed) 
			    				  output.writeBytes("true"); else
			    				  output.writeBytes("false");
			    			  is_changed = false;
			    		  } else if (path.equals("get_new.act")) {
			    			  output.writeBytes(construct_http_header(200, 7));
			    			  new_content += "</messages>";
			    			  output.writeBytes(new_content);
			    			  new_content = "<messages>";
			    		  } else if (path.equals("get_threads.act")) {
			    			  output.writeBytes(construct_http_header(200, 7));
			    			  output.writeBytes(prepare_xml_threads());
			    		  } else if(path.endsWith("get_conversation.act")) {
			    			  String id = path.substring(0, path.indexOf("_"));
			    			  output.writeBytes(construct_http_header(200, 7));
			    			  output.writeBytes(prepare_xml_conversation(id));
			    		  }
			    	  } else if (type_is == 0) {
			    		  output.writeBytes(construct_http_header(404,0));
				    	  output.writeBytes("Rozszerzenie nieobsługiwane.");  
			    	  } else {
			    		  output.writeBytes(construct_http_header(200, type_is));
			    		  String folder_name;
			    		  if ((type_is ==1) || (type_is == 2) || (type_is == 8)) {
			    			  folder_name = "images/";
			    		  } else {
			    			  folder_name = "html/";
			    		  }
			    		  InputStream is;
			    		  
			    		  if ((is = getAssets().open(folder_name + path)) != null) {
			    			  try {
			    				  byte[] buffer = new byte[1024];
					    	        int len1 = 0;
					    	        while ( (len1 = is.read(buffer)) > 0 ) {
					    	            output.write(buffer,0, len1);
					    	        }
					    	    is.close();
			    			  } catch (Exception e) {  
			    			  }
			    		  } else {
			    			  output.writeBytes(construct_http_header(404,0));
			    			  output.writeBytes("Brak pliku.");  
			    		  }
			    	  }
				      
				} else {
					output.writeBytes(construct_http_header(404,0));
			    	output.writeBytes("Nagłówek nieobsługowany.");
				}
				
			} catch (Exception e) {
				
			}
		}
		
		private String prepare_xml_conversation(String id) {
			 String s = "<conversation>";
			 Cursor messages; 
			 
			 String[] projection = new String[] {
		                "type",
		                "body",
		                "date"
		                };
			 
		     messages = getContentResolver().query(Uri.parse("content://sms/"),projection, "thread_id LIKE '" + id.toString() + "'", null, "date ASC");
		     

		     while (messages.moveToNext()) {
		    	 s += "<message type='" + messages.getString(0) + "' time='" + messages.getString(2) + "'>" + 
		    	 	stringToHTMLString(messages.getString(1)) + "</message>";
		      }
		     
		     s += "</conversation>";
			 return s;
		}
		
		private String prepare_xml_threads() {
			 String s = "<cons>";
			 Cursor messages; 
			 int i;
			 /////zemienic URI na content//sms/conversations !!!!!!!!!!!!!!!!!!!!! KONIECZNIE  pola: [thread_id, msg_count, snippet]
			 String[] projection = new String[] {
		                "thread_id",
		                "address"
		                };
			 
		     messages = getContentResolver().query(Uri.parse("content://sms/"),projection, null, null, null);
		     
		     int threads[] = new int[messages.getCount()];
		     String names[] = new String[messages.getCount()];
		     String numbers[] = new String[messages.getCount()];
		        
		     while (messages.moveToNext()) {
		    	 threads[messages.getInt(0)]++;
		       	 names[messages.getInt(0)] = getContactNameFromNumber(messages.getString(1));
		       	 numbers[messages.getInt(0)] = messages.getString(1);
		      }
		      
		    for (i=0;i<threads.length;i++) {
		    	 if (threads[i] != 0)
		    		 s += "<conv id='" + i + "' number='" + numbers[i] + "' name='" + names[i] + "' msg='" + threads[i] + "' />";
		       }  
		     s += "</cons>";
			 return s;
		 }
   
		 private String getContactNameFromNumber(String number) {
				String[] projection = new String[] {
						Contacts.Phones.DISPLAY_NAME,
						Contacts.Phones.NUMBER };
		 
				Uri contactUri = Uri.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL, Uri.encode(number));
		 
				Cursor c = getContentResolver().query(contactUri, projection, null,
						null, null);
		 
				if (c.moveToFirst()) {
					String name = c.getString(c
							.getColumnIndex(Contacts.Phones.DISPLAY_NAME));
					return name;
				}
				return number;
			}
		 
		private String construct_http_header(int return_code, int file_type) {
		    String s = "HTTP/1.0 ";
		    switch (return_code) {
		      case 200:
		        s = s + "200 OK";
		        break;
		      case 404:
		        s = s + "404 Not Found";
		        break;
		      default:
		        s = s + "404 Not Found";
		        break;
		    }

		    s = s + "\r\n";
		    s = s + "Connection: close\r\n";
		    s = s + "Server: SmsViaHTTP v0\r\n";
		    switch (file_type) {
		      case 1:
		        s = s + "Content-Type: image/jpeg\r\n";
		        break;
		      case 2:
		    	  s = s + "Content-Type: image/x-png\r\n"; 
		        break;
		      case 3:
		    	  s = s + "Content-Type: text/html\r\n"; 	
		    	  break;
		      case 4:
		    	  s = s + "Content-Type: application/javascript\r\n";
		    	  break;
		      case 5:
		    	  s = s + "Content-Type: text/plain\r\n"; 	
		    	  break;
		      case 6:
		    	  s = s + "Content-Type: text/css\r\n"; 
		    	  break;
		      case 7:
		    	  s = s + "Content-Type: text/xml\r\n"; 	
		    	  break;
		      case 8:
		    	  s = s + "Content-Type: image/gif\r\n"; 
		    	  break;
		      default:
		        s = s + "Content-Type: text/html\r\n";
		        break;
		    }
		    s = s + "\r\n";
		    return s;
		  }
		
		public String stringToHTMLString(String string) {
		    StringBuffer sb = new StringBuffer(string.length());
		    int len = string.length();
		    char c;

		    for (int i = 0; i < len; i++)
		        {
		        c = string.charAt(i);

		            if (c == '"')
		                sb.append("&quot;");
		            else if (c == '&')
		                sb.append("&amp;");
		            else if (c == '<')
		                sb.append("&lt;");
		            else if (c == '>')
		                sb.append("&gt;");
		            else {
		                int ci = 0xffff & c;
		                if (ci < 160 )
		                    sb.append(c);
		                else {
		                    sb.append("&#");
		                    sb.append(new Integer(ci).toString());
		                    sb.append(';');
		                    }
		                }
		        }
		    return sb.toString();
		}
		
		private String getLocalIpAddress() {
	        try {
	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	                NetworkInterface intf = en.nextElement();
	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
	                }
	            }
	        } catch (SocketException e) {
	        }
	        return null;
	    }

	}

}
