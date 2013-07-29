package com.pinkhatproductions.processing.android.accessory;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class AndroidAccessoryUSBManager extends Thread {
    private static final String TAG = "AndroidAccessoryUSBManager";
    
    Context _parent;
    AndroidAccessoryUSB _acc;
    boolean _running = false;
    Method _accessorySetup;
    Method _accessoryLoop;
    Method _accessoryOnConnect;
    Method _accessoryOnDisconnect;
    
    public AndroidAccessoryUSBManager(Context parent) {
        _parent = parent;
        _acc = new AndroidAccessoryUSB(parent);
        
        Class[] cArg = new Class[1];
        cArg[0] = AndroidAccessoryUSB.class;
        
        try {
            _accessorySetup = _parent.getClass().getMethod("accessorySetup", cArg);
        }
        catch(NoSuchMethodException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException("could not find defined function 'void accessorySetup(AndroidAccessoryUSB accessory)' [" + e.toString() + "]", e);
        }
        catch(NullPointerException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        catch(SecurityException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        
        try {
            _accessoryLoop = _parent.getClass().getMethod("accessoryLoop", cArg);
        }
        catch(NoSuchMethodException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException("could not find defined function 'void accessoryLoop(AndroidAccessoryUSB accessory)' [" + e.toString() + "]", e);
        }
        catch(NullPointerException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        catch(SecurityException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        
        try {
            _accessoryOnConnect = _parent.getClass().getMethod("accessoryOnConnect", cArg);
        }
        catch(NoSuchMethodException e) {
            Log.e(TAG, e.toString());
        }
        catch(NullPointerException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        catch(SecurityException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        try {
            _accessoryOnDisconnect = _parent.getClass().getMethod("accessoryOnDisconnect", new Class[] {});
        }
        catch(NoSuchMethodException e) {
            Log.e(TAG, e.toString());
        }
        catch(NullPointerException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
        catch(SecurityException e) {
            Log.e(TAG, e.toString());
            throw new RuntimeException(e);
        }
    }
    
    public AndroidAccessoryUSB getAccessory() {
        return _acc;
    }
    
    public void quit() {
        _running = false;
        interrupt();
    }
    
    boolean isConnected() {
        boolean b = _acc.isConnected();
        if(!b && _accessoryOnDisconnect != null) {
            try {
                _accessoryOnDisconnect.invoke(_parent);
            }
            catch (IllegalArgumentException e) {
                Log.e(TAG, e.toString());
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                Log.e(TAG, e.toString());
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                Log.e(TAG, e.toString());
                throw new RuntimeException(e);
            }
        }
        
        return b;
    }
    
    @Override
    public void run() {
        _running = true;
        
        while(_running) {
            while(_running && !isConnected()) {
                _acc.connect();
                try {
                    sleep(10);
                }
                catch (InterruptedException e) {
                
                }
            }
            
            if(_accessoryOnConnect != null) {
                try {
                    _accessoryOnConnect.invoke(_parent, _acc);
                }
                catch (IllegalArgumentException e) {
                    Log.e(TAG, e.toString());
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e) {
                    Log.e(TAG, e.toString());
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    Log.e(TAG, e.toString());
                    throw new RuntimeException(e);
                }
            }
            
            if(!_running) break;
            if(!isConnected()) continue;
            
            // setup
            try {
                _accessorySetup.invoke(_parent, _acc);
            }
            catch (IllegalArgumentException e) {
                Log.e(TAG, e.toString());
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                Log.e(TAG, e.toString());
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                Log.e(TAG, e.toString());
                throw new RuntimeException(e);
            }
            
            if(!_running) break;
            if(!isConnected()) continue;
            
            // loop
            while(_running && isConnected()) {
                try {
                    _accessoryLoop.invoke(_parent, _acc);
                }
                catch (IllegalArgumentException e) {
                    Log.e(TAG, e.toString());
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e) {
                    Log.e(TAG, e.toString());
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    Log.e(TAG, e.toString());
                    throw new RuntimeException(e);
                }
            }
        }
    }
}