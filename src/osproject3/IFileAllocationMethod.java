/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package osproject3;

/**
 *
 * @author iceiceninja
 */
public interface IFileAllocationMethod {
    void storeFile(String fileName, byte[] fileContents);
    byte[] retrieveFile(String fileName);
    DiskDrive getDiskDrive();
}
