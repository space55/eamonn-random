package server;

import client.ClientMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class Client implements Runnable
{
    private Thread t; // Thread used for a message listener
    private Socket s; // Socket used to connect to the server
    private BufferedReader in; // Wrapper for the socket's input stream
    private PrintWriter out; // Wrapper for the socket's output stream

    private String ip; // IP of the remote server
    private int port; // Port of the remote server

    /**
     * Creates a new Client object, with a specified Socket from the server
     * @param s Socket from the server's ServerSocket
     */
    public Client(Socket s)
    {
        this.s = s;
        try
        {
            in = new BufferedReader(new InputStreamReader(s.getInputStream())); // Initializes the BufferedReader
            out = new PrintWriter(s.getOutputStream()); // Initializes the PrintWriter

            // Note: BufferedReader takes an InputStreamReader, because BufferedWriter sucks

            t = new Thread(this); // Creates a new thread with this Client object as the Runnable
            t.start(); // Starts the thread
        }
        catch (ConnectException e) // This will be thrown when Java can't connect to the server
        {
            System.out.println("Remote server didn't accept connection");
        }
        catch (IOException e) // Catch whiny Java exceptions, that never really happen
        {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the remote client
     * @param msg Message to be sent
     */
    public void send(String msg)
    {
        out.println(msg);
        out.flush(); // You have to flush, because PrintWriter sucks

        // If you don't flush, the stuff isn't sent. It's just added to the PrintWriter's internal buffer
    }

    /**
     * Reads a line from the socket's InputStream
     * @return Message received from (hopefully) the remote client
     */
    public String read()
    {
        try
        {
            return in.readLine(); // Reads and returns
        }
        catch (SocketException e) // Gets thrown when server disconnects
        {
            System.out.println("Server disconnected");
        }
        catch (IOException e) // Because Java likes these
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Don't touch this method. It is the Thread object's run code
     */
    public void run()
    {
        while (true) // Infinite loop to read
        {
            String temp = read(); // Reads
            if (temp != null) // If it's not equal to null...
            {
                ServerMain.gotMessage(temp); // We got a message!
            }
        }
    }
}
