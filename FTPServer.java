// FTP Server

import java.net.*;
import java.io.*;
import java.util.*;

public class FTPServer
{
    public static void main(String args[]) throws Exception
    {
        ServerSocket soc=new ServerSocket(5217);
        System.out.println("FTP Server Started on Port Number 5217");
        while(true)
        {
            System.out.println("Waiting for Connection ...");
            transferfile t=new transferfile(soc.accept());
            
        }
    }
}

class transferfile extends Thread
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    
    transferfile(Socket soc)
    {
        try
        {
            ClientSoc=soc;                        
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();
            
        }
        catch(Exception ex)
        {
        }        
    }
    void SendFile() throws Exception
    {        
        String filename=din.readUTF();
        File f=new File(filename);
       
            FileInputStream fin=new FileInputStream(f);
            int ch;
            do
            {
                ch=fin.read();
                dout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);    
            fin.close();                           
        }
    
    
    void ReceiveFile() throws Exception
    {
        String fileName=din.readUTF();
        File f=new File(fileName);
        
             FileOutputStream fout=new FileOutputStream(f);
                int ch;
                String temp;
                do
                {
                    temp=din.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);                    
                    }
                }while(ch!=-1);
                fout.close();
                dout.writeUTF("File Send Successfully");
            }
            
            
    


    public void run()
    {
        while(true)
        {
            try
            {
            String Command=din.readUTF();
            if(Command.compareTo("GET")==0)
            {
                SendFile();
                continue;
            }
            else if(Command.compareTo("SEND")==0)
            {
                ReceiveFile();
                continue;
            }
            else if(Command.compareTo("DISCONNECT")==0)
            {
                System.exit(1);
            }
            }
            catch(Exception ex)
            {
            }
        }
    }
}