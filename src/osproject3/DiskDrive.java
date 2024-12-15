/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osproject3;

/**
 *
 * @author iceiceninja
 */
public class DiskDrive {

    //Each block has 512 bytes
    byte[][] blockArray = new byte[256][512];

    public DiskDrive() {
//            byte[] blockArray = new byte[512];
//            First Block is file allocation table
//              Second Block is bitmap for free space management
//              remaining blocks hold data for files 
    }

    public byte[] readBlock(int blockNum) {
        return blockArray[blockNum];
    }

    public void writeBlock(int blockNum, byte[] bytesToWrite) {
//        System.arraycopy(blockArray[blockNum], 0, bytesToWrite, 0, blockArray[blockNum].length); // This might make files larger than they need to be
        blockArray[blockNum] = bytesToWrite;
    }
}
