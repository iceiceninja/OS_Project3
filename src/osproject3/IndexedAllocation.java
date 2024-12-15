/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osproject3;

import java.util.Arrays;

/**
 *
 * @author iceiceninja
 */
public class IndexedAllocation implements IFileAllocationMethod {

     int startBlock;// = 2;
    final int FileAllocationTable = 0;
    final int Bitmap = 1;
    final static DiskDrive diskDrive = new DiskDrive();

    public IndexedAllocation() {
        //init file allocation table and bitmap
//        diskDrive.writeBlock(1, );
        byte[] BitmapContents = diskDrive.readBlock(Bitmap);
        BitmapContents[0] = 1;
        BitmapContents[1] = 1;
        diskDrive.writeBlock(Bitmap, BitmapContents);
    }

    @Override
    public void storeFile(String fileName, byte[] fileContents) {
        int fileBlockLength = fileContents.length % 512 != 0 ? (fileContents.length / 512)/*+1*/ : fileContents.length / 512;//(int) (Math.ceil((fileContents.length / 512)));

        /*
            HAVE A CHECK USING BIT MAP THAT CHECKS THE FIRST EMPTY BLOCK (zero) AND MAKES SURE THERE IS ENOUGH 
            CONTIGUOUS BLOCKS AFTER TO HOLD THE WHOLE FILE. LIKE IF A FILE IS THREE BLOCKS LONG AND THE BITMAP LOOKS LIKE
            
            11011101111000111 THAT IT SKIPS THE FIRST TWO EMPTY BLOCKS BECAUSE IT WOULDN'T BE ABLE TO FIT THE WHOLE FILE WITHOUT
            OVERWRITING 
        
            ALSO UPON FILE DELETION MAKE SURE TO UPDATE FAT AND BITMAP
         */
        byte[] bitmapContents = diskDrive.readBlock(Bitmap);
        for (int i = 0; i < bitmapContents.length; i++) {
            if (bitmapContents[i] == 0) {
                boolean isValid = true;
                for (int j = i; j <= i + fileBlockLength; j++) {
                    if (bitmapContents[j] != 0) {
                        isValid = false;
                    }
                }
                if (isValid == true) {
                    startBlock = i;
                    //set all bits used up to 1
                    for (int j = i; j <= i + fileBlockLength; j++) {
                        bitmapContents[j] = 1;
                    }
                    break;
                    //leave for loop

                }
            }
        }

        //need start block, length, and FileName
//        diskDrive.writeBlock(startBlock, fileContents);
        //if disk drive successfully allocates and stores the file then update the FAT and bitmap
        //  maybe retrieve the FAT block first and then appending the new file's info onto the end of it
        byte[] FATContents = diskDrive.readBlock(FileAllocationTable);

        //FAT is broken down like this. File name is 8 bytes, start block is 1 byte, and length is 1 byte. 10 bytes per FAT entry
        //        FATContents = fileName.getBytes() + fileBlockLength + (byte) startBlock;
        int counter = 0;
        int i = 0;
        boolean writeToFAT = true;
        while (writeToFAT) {
            if (FATContents[i] == 0 && counter < 8) {
                if (counter < fileName.getBytes().length) {
                    FATContents[i] = fileName.getBytes()[counter];  //if filename bytes is less than 8 then this will cause error
                    counter++;
                    i++;
                } else {
                    counter = 8;
                    i += 8 - fileName.getBytes().length;
                }

            } else if (counter == 8 /*|| counter >= fileName.getBytes().length*/) {
                FATContents[i] = (byte) startBlock;
                FATContents[i + 1] = (byte) fileBlockLength;
                writeToFAT = false;
                counter++;

            } else if (FATContents[i] != 0) {
                i++;
            } else {
                i += 10; //skips a whole FAT record each time the first byte in non zero

            }

        }
        diskDrive.writeBlock(FileAllocationTable, FATContents);
        //Write to bitmap now   COMMENTED THE NEXT FEW LINES OUT BECAUSE I REDID ABOVE. IF ANY WEIRD BEHAVIOR HAPPENS UNCOMMENT
        //        byte[] BitmapContents = diskDrive.readBlock(Bitmap);
        //        for (int bit = startBlock; bit <= startBlock + fileBlockLength; bit++) {
        //            BitmapContents[bit] = 1;
        //        }
        //        diskDrive.writeBlock(Bitmap, BitmapContents);
        //        startBlock += fileBlockLength;
        int test = startBlock;
        int test2 = startBlock + fileBlockLength;
        int blockCounter = 0;
        for (int block = startBlock; block <= startBlock + fileBlockLength; block++) {
//            diskDrive.writeBlock(startBlock, fileContents);
            byte[] blockToWrite = Arrays.copyOfRange(fileContents, blockCounter * 512, (blockCounter + 1) * 512);
            blockCounter++;
            diskDrive.writeBlock(block, blockToWrite);
        }

    }

    @Override
    public byte[] retrieveFile(String fileName) {
        // Use file allocation table to search for filename, then find its start block and file length. Then ask for all blocks from start to start+length
        byte[] file;
        byte[] FAT = diskDrive.readBlock(FileAllocationTable);
        boolean searchForFile = true;
        byte fileStartBlock = 0;
        byte fileLength = 0;

        int counter = 0;
        while (searchForFile) {
//            FAT[counter] 
            byte[] testForName = Arrays.copyOfRange(FAT, counter, counter + fileName.getBytes().length);
            if (Arrays.equals(testForName, fileName.getBytes())) {
                searchForFile = false;
                fileStartBlock = FAT[counter + 8 /*fileName.getBytes().length*/]; // 
                fileLength = FAT[counter + 9 /*fileName.getBytes().length*/]; // + 9

            } else {
                counter += 10;
            }
//            if (FAT[counter] == fileName.getBytes()[0]) { // only check for filename if the current byte matches the fileName's first byte
//                byte[] testForName = Arrays.copyOfRange(FAT, counter, counter + fileName.getBytes().length); // this might cause an issue if one file is abc and another is abcd then it will think abcd is abc and vice versa
//                if (Arrays.equals(testForName, fileName.getBytes())) {
//                    fileStartBlock = FAT[counter + 8];
//                    fileLength = FAT[counter + 9];
//                    searchForFile = false;
//                }
//            }
            counter++;
        }
        file = new byte[(fileLength + 1) * 512]; // how many blocks long times how large a block is
        int fileBlockCounter = 0;
        for (int i = (int) fileStartBlock; i <= (int) fileStartBlock + fileLength; i++) {
            byte[] fileFromDisk = diskDrive.readBlock(i);
            System.arraycopy(fileFromDisk, 0, file, (fileBlockCounter * 512), fileFromDisk.length); // issue currently: Need to have the file byte array become what is returned from the readBlock calls (it needs to append each one)
            fileBlockCounter++;
        }
        return file;
    }

    @Override
    public DiskDrive getDiskDrive() {
        return diskDrive;
    }

}
