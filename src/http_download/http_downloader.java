package http_download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;

//--------<START for http_downloader constructor and initialization-------->

class http_dwnld implements Runnable
{
	private String url;
    private int num;
    private int start;
    private int end;
    private String fileName;

    public http_dwnld(String fileName,String url, int num, int start, int end) 
    {
	        this.url = url;
	        this.num = num;
	        this.start = start;
	        this.end = end;
	        this.fileName = fileName;
		} 
 
	 @Override 
	 public void run() 
	 {
	
		 try
		 {
			 http_filedwnld();
			 
			
		 } 

		 catch (IOException e)
		 {
			 e.printStackTrace();
		 }
		 
	
	 }
	 
	//--------<START for http_filedwnld which requests file and download it from the URL-------->

	 private void http_filedwnld()throws IOException 
	 {
		 URL file = new URL(url);
		 //System.out.println("Requesting Download from: " + url);
		 HttpURLConnection con = (HttpURLConnection) file.openConnection(); //open HTTP connection

		 String str = start + "-" + end;
		 int buf = end - start;

		 con.setRequestProperty("Range","bytes=" + str);

		 int rc = con.getResponseCode();
	
		 if (rc == 206) 
			 
		 {
			 	
			 InputStream is = con.getInputStream();
	
			 FileOutputStream os = new FileOutputStream("part_" + num + "-" + fileName);

		
			 byte[] buffer = new byte[buf];
			 is.read(buffer);
			 
			 os.write(buffer);

			 os.close();
			 is.close();
	
			 System.out.println("Partition: " + num + " downloaded");
			 
			 
		} 
		 
		 
		 
		 else 
		 {
			System.out.println("404 : File Not Found");
		 }
		 
		 
		 con.disconnect();									//close HTTP connection
		 
		 
	 }
	
	 
	//--------<END for http_filedwnld()-------->
	 
}

//--------<END for http_downloader-------->


//--------<START for http_downloader main class and initialization-------->

public class http_downloader 

{

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		String url = args[0];
		String no = args[1];
	    int num = Integer.parseInt(no);
	    
		URL f = null;
		f = new URL(url);

        int size = f.openConnection().getContentLength();

        System.out.println();
        System.out.println("Size of File: " + size);
        //ExecutorService es = Executors.newFixedThreades(5);

        ExecutorService es = Executors.newFixedThreadPool(10);
        int totalsize = size / num;
        int r = size % num;
        int a = -1;
        //int a = 0;

        System.out.println("Partition Size: " + num);
        System.out.println();
        
        String fileName = "";
		
		fileName = url.substring(url.lastIndexOf("/") +1, url.length());

        for(int i=1; i<=num; i++)
        {

        	if(num == i)
        	{
        		es.submit(new http_dwnld(fileName, url, i, a+1, (totalsize*i)+r));
        	}

        	es.submit(new http_dwnld(fileName, url, i, a+1, totalsize*i));
        	a = a + totalsize;
        }
        

        es.shutdown();
         
		Thread.sleep(2500); 
		
        merge(num, fileName);

        System.out.println();
        System.out.println("Merging Completed!!");
        System.out.println();
        System.out.println("Your file is ready to view..");
        System.out.println();
	}
	
	
	//--------<END for http_downloader main class-------->
	
	
	//--------<START for merge() functions to merge downloaded files-------->
	
	
	public static void merge(int num, String fileName) 
	
	{

		File openfile = new File("merged-" + fileName);
		FileOutputStream fos;
		FileInputStream fis;
		byte[] fileBytes;
		int bytesRead = 0;
		List<File> list = new ArrayList<File>();

		for(int i = 1; i <= num; i++)
		{

		list.add(new File("part_"+ i + "-" + fileName));

		}

		try {

		    fos = new FileOutputStream(openfile,true);

		    for (File file : list)

		    {
		        fis = new FileInputStream(file);
		        int a=(int)file.length();
		        fileBytes = new byte[a];
		        
		        bytesRead = fis.read(fileBytes, 0, a);
		        assert(bytesRead == fileBytes.length);
		        assert(bytesRead == a);
		        fos.write(fileBytes);
		        fos.flush();
		        fileBytes = null;
		        fis.close();
		        fis = null;
		    }

		    fos.close();
		    fos = null;

		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//--------<END for merge() function-------->

}

