package org.example;

import org.example.OS.OperationStatus;
import org.example.OS.Status;
import org.example.WD.WorkingDirectory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        String[] prevInputs; // библиотека JUndo
        String userInput;

        String curDir = Paths.get("").toAbsolutePath().toString();
        WorkingDirectory WD = WorkingDirectory.getInstance(curDir);
        System.out.print(curDir + ">");

        OperationStatus os = new OperationStatus();

        boolean exitProg = false;
        while (!exitProg)
        {
            userInput = sc.nextLine();
            switch (userInput)
            {
                case "q":
                {
                    exitProg = true;
                }
                break;

                case "dir":
                {
                    os = WD.getContents();
                }
                break;

                case "cd ..":
                {
                    os = WD.cdParent();
                    curDir = WD.getCurDir();
                }
                break;

                case "tree":
                {
                    os = WD.getDirTree(0);
                }
                break;

                default:
                {
//                    File[] roots = File.listRoots();
//                    boolean isRoot = false;
//                    for (File root : roots)
//                    {
//                        System.out.println(root.toString());
//                        if (userInput.equals(root.getAbsolutePath().substring(0, 2)))
//                        {
//                            isRoot = true;
//                            break;
//                        }
//                    }

                    if (userInput.startsWith("cd "))
                    {
                        String dirName = userInput.substring(3).trim();
                        os = WD.cdChild(dirName);
                        curDir = WD.getCurDir();
                    }
                    else if (userInput.startsWith("mkdir "))
                    {
                        String dirName = userInput.substring(6).trim();
                        os = WD.makeDir(dirName);
                    }
                    else if (userInput.startsWith("rmdir "))
                    {
                        String dirName = userInput.substring(6).trim();
                        os = WD.deleteWithSubdir(dirName);
                    }
//                    else if (isRoot)
//                    {
//                        String dirName = userInput.substring(2).trim();
//                        os = WD.changeDir(dirName);
//                        curDir = WD.getCurDir();
//                    }
                    else
                    {
                        os.setStatus(Status.ERROR);
                        os.setMessage("\"" + userInput + "\"" + " не является командой, программой или файлом");
                    }
                }
                break;
            }

            if (!exitProg)
            {
                System.out.println(os.toString());
                System.out.print(curDir + ">");
            }
        }
    }
}