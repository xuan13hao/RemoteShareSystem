package com.ccit.util;

import com.sun.jna.Structure;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.User32.HHOOK;
import com.sun.jna.examples.win32.User32.HOOKPROC;
import com.sun.jna.examples.win32.User32.MSG;
import com.sun.jna.examples.win32.W32API.HMODULE;
import com.sun.jna.examples.win32.W32API.LRESULT;
import com.sun.jna.examples.win32.W32API.WPARAM;

public class MouseHook extends Thread
{
    public static final int WM_MOUSEMOVE = 512;
    public static final int WM_LBUTTONDOWN = 513;
    public static final int WM_LBUTTONUP = 514;
    public static final int WM_RBUTTONDOWN = 516;
    public static final int WM_RBUTTONUP = 517;
    public static final int WM_MBUTTONDOWN = 519;
    public static final int WM_MBUTTONUP = 520;
    
    private static HHOOK hhk;
    private  Mousexy xy;
	private static LowLevelMouseProc mouseHook;

	public MouseHook(Mousexy xy)
	{
		this.xy=xy;
	}
	
	public void run()
	{
		    final User32 lib = User32.INSTANCE;
	        HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
			mouseHook= new LowLevelMouseProc() 
			{
		        public LRESULT callback(int nCode, WPARAM wParam,MOUSEHOOKSTRUCT info) 
		        {
		            if (nCode >= 0) 
		            {
		                switch (wParam.intValue()) 
		                {
			                case MouseHook.WM_MOUSEMOVE:
			                {
			                	xy.setX(info.pt.x);
			                	xy.setY(info.pt.y);
			                   // System.err.println("in callback,  x=" + MouseHook.x + " y=" +MouseHook.y);
			                }
		                }
		            }
		            return lib.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
		        }
		    };
		    hhk = lib.SetWindowsHookEx(User32.WH_MOUSE_LL, mouseHook, hMod, 0);
		    int result;
	        MSG msg = new MSG();
	        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) 
	        {
	            if (result == -1) {
	                System.err.println("error in get message");
	                break;
	            } else {
	                System.err.println("got message");
	                lib.TranslateMessage(msg);
	                lib.DispatchMessage(msg);
	            }
	        }
	        lib.UnhookWindowsHookEx(hhk);
	}
	 

   public interface LowLevelMouseProc extends HOOKPROC {
       LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT lParam);
   }

   public class Point extends Structure
   {
       public class ByReference extends Point implements Structure.ByReference
       {
       };
       public com.sun.jna.NativeLong x;
       public com.sun.jna.NativeLong y;
   }

   public static class MOUSEHOOKSTRUCT extends Structure 
   {
       public static class ByReference extends MOUSEHOOKSTRUCT implements
               Structure.ByReference {
       };

       public User32.POINT pt;
       public User32.HWND hwnd;
       public int wHitTestCode;
       public User32.ULONG_PTR dwExtraInfo;
   }

}
