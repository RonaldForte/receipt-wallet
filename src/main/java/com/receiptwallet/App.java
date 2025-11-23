package com.receiptwallet;

import com.receiptwallet.model.Receipt;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Receipt r = new Receipt("Target", 23.45, "11-23-2025");
        System.out.println(r);

    }
}
