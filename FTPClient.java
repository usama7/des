// FTP Client

import java.net.*;
import java.io.*;
import java.util.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;



class FTPClient
{
  public static void main(String args[]) throws Exception
    {
        Socket soc=new Socket("127.0.0.1",5217);
        transferfileClient t=new transferfileClient(soc);
        t.displayMenu();
        
    }
}
class transferfileClient
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;
    transferfileClient(Socket soc)
    {
        try
        {
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            br=new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex)
        {
        }        
    }
    void SendFile() throws Exception
    {        
        
        String filename;
        System.out.print("Enter File Name :");
        filename=br.readLine();
        FileInputStream fis = new FileInputStream(filename);
        FileOutputStream fos = new FileOutputStream("encrypted.txt");
        String key = "revealit";
        encrypt(key, fis, fos);
        dout.writeUTF(filename);
        File f=new File("encrypted.txt");
        
        
        System.out.println("Sending File ...");
        FileInputStream fin=new FileInputStream(f);
        int ch;
        do
        {
            ch=fin.read();
            dout.writeUTF(String.valueOf(ch));
        }
        while(ch!=-1);
        fin.close();
        System.out.println(din.readUTF());

        
    }
    
    void ReceiveFile() throws Exception
    {
        String fileName;
        System.out.print("Enter File Name :");
        fileName=br.readLine();
       FileInputStream fis2 = new FileInputStream(fileName);
       FileOutputStream fos2 = new FileOutputStream("decrypted.txt");
        String key = "revealit";
         // decrypt(key, fis2, fos2);
        dout.writeUTF(fileName);
       // File f=new File("decrypted.txt");
       File f=new File(fileName);
            System.out.println("Receiving File ...");
            
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
decrypt(key, fis2, fos2);

            System.out.println(din.readUTF());
                
        }
        
        
    

    public void displayMenu() throws Exception
    {
        while(true)
        {    
            System.out.println("[ MENU ]");
            System.out.println("1. Send File");
            System.out.println("2. Receive File");
            System.out.println("3. Exit");
            System.out.print("\nEnter Choice :");
            int choice;
            choice=Integer.parseInt(br.readLine());
            if(choice==1)
            {
                dout.writeUTF("SEND");
                SendFile();
            }
            else if(choice==2)
            {
                dout.writeUTF("GET");
                ReceiveFile();
            }
            else
            {
                dout.writeUTF("DISCONNECT");
                System.exit(1);
            }
        }
    }

public static void encrypt(String key, InputStream is, OutputStream os) throws Exception {
		encryptOrDecrypt(key, Cipher.ENCRYPT_MODE, is, os);
	}

public static void decrypt(String key, InputStream is, OutputStream os) throws Exception {
		encryptOrDecrypt(key, Cipher.DECRYPT_MODE, is, os);
	} 


public static void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os) throws Exception  {

		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = skf.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE

		if (mode == Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, desKey);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			doCopy(cis, os);
		} else if (mode == Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, desKey);
			CipherOutputStream cos = new CipherOutputStream(os, cipher);
			doCopy(is, cos);
		}
	}

	public static void doCopy(InputStream is, OutputStream os) throws IOException  {
		byte[] bytes = new byte[64];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			os.write(bytes, 0, numBytes);
		}
		os.flush();
		os.close();
		is.close();
	}


}

