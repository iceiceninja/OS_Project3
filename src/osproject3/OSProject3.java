/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package osproject3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author iceiceninja
 */
public class OSProject3 {

    /**
     * @param args the command line arguments
     */
    static String fileName;
    static String copyFrom;
    static String copyTo;
    static IFileAllocationMethod File_System;

    public static void main(String[] args) {

        // TODO code application logic here
        final String ALLOCATION_METHOD = args[0];
        switch (ALLOCATION_METHOD) {
            case "contiguous":
                File_System = new ContiguousAllocation();
                break;
            case "chained":
                File_System = new ChainedAllocation();
                break;
            case "indexed":
                File_System = new IndexedAllocation();
                break;
            default:
                System.out.println("Allocation method: " + ALLOCATION_METHOD + " Not recognized.");
        }
        Scanner in = new Scanner(System.in);
        String input;
        do {
            System.out.println("1) Display a file\n"
                    + "2) Display the file table\n"
                    + "3) Display the free space bitmap\n"
                    + "4) Display a disk block\n"
                    + "5) Copy a file from the simulation to a file on the real system\n"
                    + "6) Copy a file from the real system to a file in the simulation\n"
                    + "7) Delete a file\n"
                    + "8) Exit");
            input = in.nextLine();
            System.out.println("Choice: " + input);
            switch (input) {

                case "1":
                    System.out.print("File Name: ");
                    fileName = in.nextLine();
                    displayFile(fileName);
                    break;

                case "2":
                    displayFileTable();
                    break;

                case "3":
                    displayFreeSpaceBitmap();
                    break;

                case "4":
                    System.out.print("Block Number: "); //unsure if this is how he wants this handled.
                    String blockNumber = in.nextLine();
                    displayDiskBlock(blockNumber);
                    break;

                case "5":
                    System.out.print("Copy From: ");
                    copyFrom = in.nextLine();
                    System.out.print("Copy To: ");
                    copyTo = in.nextLine();
                    copySimFileToReal(copyFrom, copyTo);
                    System.out.println("\nFile " + copyFrom + " copied");
                    break;

                case "6":
                    System.out.print("Copy From: ");
                    copyFrom = in.nextLine();
                    System.out.print("\nCopy To: ");
                    copyTo = in.nextLine();
                    if (copyTo.getBytes().length > 8) {
                        System.out.println("File name cannot be longer than 8 characters.");
                        break;
                    }
                    copyFileFromRealToSim(copyFrom, copyTo);
                    System.out.println("\nFile " + copyFrom + " copied");
                    break;
                case "7":
                    System.out.print("File Name: ");
                    fileName = in.nextLine();
                    deleteFileFromSim(fileName);
                    break;
                case "8":
                    break;
            }
        } while (!"8".equals(input));

    }

    private static void displayDiskBlock(String blockNumber) {
        System.out.println(Arrays.toString(File_System.getDiskDrive().readBlock(Integer.parseInt(blockNumber))));
    }

    private static void displayFile(String fileName) {
        byte[] file = File_System.retrieveFile(fileName);
        for (int i = 0; i < file.length; i++) {
            System.out.print((char) file[i]);
//            String test = new String(file, StandardCharsets.UTF_8);
            
//            System.out.println(test);
        }
        System.out.print("\n");
//        System.out.println(Arrays.toString());
    }

    private static void displayFileTable() {
        //Idk how to do the store and retrieve files yet, but if we do it using names then we can have this name map to the first disk drive block.
        //  Try to find work around, since this blocks naming files FileTable and FreeSpaceBitmap
//        byte[] fileContents = File_System.retrieveFile("FileTable");
//        System.out.println(Arrays.toString(fileContents));
//        System.out.println(Arrays.toString());
        byte[] FATContents = File_System.getDiskDrive().readBlock(0);
//        boolean displayFAT = true;
        int recordCounter=0;
        System.out.println("FILE NAME_START BLOCK_LENGTH");
        while(FATContents[recordCounter] != 0)
        {
            for(int i = recordCounter; i < 10; i++)
            {
                if(i != 8 && i != 9)
                {
                    System.out.print((char)FATContents[i]);
                }else
                {
                    System.out.print("_" + (int) FATContents[i]);
                }
            }
            System.out.print("\n");
            recordCounter +=10;
        }
    }

    private static void displayFreeSpaceBitmap() {
        //Idk how to do the store and retrieve files yet, but if we do it using names then we can have this name map to the second disk drive block.
//        byte[] fileContents = File_System.retrieveFile("FreeSpaceBitmap");
        System.out.println(Arrays.toString(File_System.getDiskDrive().readBlock(1)));
    }

    private static void copySimFileToReal(String copyFrom, String copyTo) {
        //get name and bytes from real file
        byte[] fileContents = File_System.retrieveFile(copyFrom); // Files are 
        try (FileOutputStream fos = new FileOutputStream("./" + copyTo)) {
            // Write the byte data to the file
            fos.write(fileContents);

            System.out.println("Bytes written to file successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFileFromRealToSim(String copyFrom, String copyTo) {
        File file = new File(copyFrom);
        try (FileInputStream fis = new FileInputStream("./" + file)) {
            // Create a byte array to store the read data
            byte[] fileContents = new byte[(int) file.length()];

            // Read bytes from the file into the byte array
            int bytesRead = fis.read(fileContents);

            // Display the number of bytes read
            System.out.println("Number of bytes read: " + bytesRead);

            // Process the byte array as needed
            // ...
            File_System.storeFile(copyTo, fileContents);

        } catch (IOException e) {
            e.printStackTrace();

        }
        // Get and store length of real file so that the file stays the same length if you take it out

        // copyFrom now holds the name of the file and fileContents should hold the bytes from the file
        //now just have real system make a file and write the bytes to it
    }

    private static void deleteFileFromSim(String fileName) {
        //  maybe use the below method, but store a empty byte array over a file to "delete" it? Or straight up make a delete file method in filesystem
        //  File_System.storeFile(copyTo, fileContents);
        
    }

}
