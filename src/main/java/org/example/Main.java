package org.example;

import org.example.OS.OperationStatus;
import org.example.WD.WorkingDirectory;

import java.nio.file.Paths;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        String userInput;

        String curDir = Paths.get("").toAbsolutePath().toString();
        WorkingDirectory WD = WorkingDirectory.getInstance(curDir);
        System.out.print(curDir + ">");

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
                    WD.getContents();
                }
                break;

                case "cd ..":
                {
                    WD.cdParent();
                    curDir = WD.getCurDir();
                }
                break;

                default:
                {
                    OperationStatus os = new OperationStatus();

                    if (userInput.startsWith("cd "))
                    {
                        String dirName = userInput.substring(3).trim();
                        WD.cdChild(dirName, os); // ???
                        curDir = WD.getCurDir();
                    }
                    else if (userInput.startsWith("mkdir "))
                    {
                        String dirName = userInput.substring(6).trim();
                        WD.makeDir(dirName);
                    }
                    else System.out.println("\"" + userInput + "\"" + " не является командой, программой или файлом");

                    System.out.println(os.toString());
                }
                break;
            }

            if (!exitProg) System.out.print(curDir + ">");
        }
    }
}