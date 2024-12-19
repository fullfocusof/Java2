package org.example;

import org.example.Article.Article;
import org.example.OS.OperationStatus;
import org.example.OS.Status;
import org.example.WD.WorkingDirectory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        OperationStatus os = new OperationStatus();
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
                case "exit":
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

                case "help":
                {
                    String sb = String.format("%-50s %s%n", "dir", "Вывести содержимое каталога") +
                            String.format("%-50s %s%n", "tree", "Графически отображает структуру каталогов диска или пути") +
                            String.format("%-50s %s%n", "cd ..", "Перейти в родительский каталог") +
                            String.format("%-50s %s%n", "cd <абсолютный_путь_к_каталогу>", "Перейти в каталог по пути") +
                            String.format("%-50s %s%n", "cd <имя_каталога/путь_к_дочернему_каталогу>", "Перейти в дочерний каталог с заданным именем или по относительному пути") +
                            String.format("%-50s %s%n", "mkdir <название_каталога>", "Создать каталог с заданным именем") +
                            String.format("%-50s %s%n", "rmdir <название_каталога>", "Удалить дочерний каталог с заданным именем") +
                            String.format("%-50s %s%n", "parse <название_файла>", "Загрузить статьи из файла с сохранением в формате json и PDF") +
                            String.format("%-50s %s%n", "exit", "Выйти из программы");
                    System.out.println(sb);
                }
                break;

                default:
                {
                    if (userInput.startsWith("cd "))
                    {
                        String dirName = userInput.substring(3).trim();
                        if (dirName.contains(":")) os = WD.changeDir(dirName);
                        else os = WD.cdChild(dirName);
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
                    else if (userInput.startsWith("parse "))
                    {
                        String fileName = userInput.substring(6).trim();
                        String filePath = curDir + "\\" + fileName;
                        List<Article> arts = Article.getFromFile(filePath);
                        for (Article art : arts)
                        {
                            System.out.println(art.toString() + "\n");
                        }

                        Article.toGsonFile(arts, "outputGson.json");
                        Article.toJacksonFile(arts, "outputJackson.json");
                        Article.toPDFFile(arts, "outputPDF.pdf");

                        os.setStatus(Status.OK);
                        os.setMessage("Преобразование завершено");
                    }
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
                if (!userInput.equals("help")) System.out.println(os.toString());
                System.out.print(curDir + ">");
            }
        }
    }
}