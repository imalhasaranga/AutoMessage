/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hello;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.MessageConnection;

/**
 * @author Imal
 */
public class Message extends MIDlet implements CommandListener{

    private Display d;
    private Form f;
    private Command exit;
    private Command save;

    private TextField t1;
    private TextField tbox;
    private StringItem item;
    private RecordStore store;

    public Message(){

        d = Display.getDisplay(this);
        f = new Form("AutoMessaging");
        item = new StringItem(null, "Please set configurations and save");

        f.append(item);
        t1 = new TextField("number", null, 20, TextField.NUMERIC);
        f.append(t1);
        tbox = new TextField("message", null, 60, TextField.ANY);
        f.append(tbox);

        exit = new Command("Exit", Command.EXIT, 0);
        save = new Command("Save", Command.OK, 1);
        f.addCommand(exit);
        f.addCommand(save);
        f.setCommandListener(this);




    }





    public void startApp() {
        try {
            store = RecordStore.openRecordStore("message", true);
        } catch (Exception ex) {
            System.out.println(ex);

        }

        try {
          final String number = new String(store.getRecord(1));
          final String text = new String(store.getRecord(2));

           new Thread(new Runnable() {

                public void run() {
                    try {
                MessageConnection con = (MessageConnection) Connector.open("sms://" +number+ ":1234");
                BinaryMessage tm = (BinaryMessage) con.newMessage(con.BINARY_MESSAGE);
                tm.setPayloadData(text.getBytes());
                con.send(tm);
                con.close();
            } catch (Exception e) {
                System.out.println(e);
            }
                }
            }).start();

        } catch (Exception e) {
            d.setCurrent(f);
        }

        
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {

        if(c == exit){

            notifyDestroyed();
        }else if(c == save){
            String number = t1.getString();
            String text = tbox.getString();
            try {
               store.addRecord(number.getBytes(), 0, number.getBytes().length);
               store.addRecord(text.getBytes(), 0, text.getBytes().length);

            } catch (Exception e) {
                System.out.println(e);
            }




        }

    }
}
