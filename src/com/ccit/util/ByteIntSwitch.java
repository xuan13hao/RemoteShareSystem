package com.ccit.util;


public final class ByteIntSwitch {

    public static void main(String args[] ) {
        int i = 212123;
        byte[] b = toByteArray(i, 4);   //整型到字节，

       System.out.println( "212123 bin: " + Integer.toBinaryString(212123));//212123的二进制表示
       System.out.println( "212123 hex: " + Integer.toHexString(212123));  //212123的十六进制表示  

        for(int j=0;j<4;j++){
              System.out.println("b["+j+"]="+b[j]);//从低位到高位输出,java的byte范围是-128到127
        }
       
        int k=toInt(b);//字节到整型，转换回来
        System.out.println("byte to int:"+k); 
      
    }

    
    // 将iSource转为长度为iArrayLen的byte数组，字节数组的低位是整型的低字节位
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for ( int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte)( iSource>>8*i & 0xFF );
          
        }
        return bLocalArr;
    }   

     // 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        
        for ( int i =0; i<4 ; i++) {
            bLoop = bRefArr[i];
            iOutcome+= (bLoop & 0xFF) << (8 * i);
          
        }  
        
        return iOutcome;
    }   
    
}  
