package org.example.WD;

import org.example.OS.OperationStatus;
import org.example.OS.Status;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Comparator;
import java.util.List;

public class WorkingDirectory
{
    private static WorkingDirectory instance;
    private String directoryName;

    private WorkingDirectory(String directoryNameInput)
    {
        directoryName = directoryNameInput;
    }

    public static WorkingDirectory getInstance(String directoryNameInput)
    {
        if (instance == null) instance = new WorkingDirectory(directoryNameInput);
        return instance;
    }

    public String getCurDir()
    {
        return directoryName;
    }

    public OperationStatus getContents()
    {
        OperationStatus os = new OperationStatus();
        StringBuilder data = new StringBuilder();

        Path curDir = Paths.get(directoryName);

        try
        {
            List<Path> entries = Files.list(curDir)
                    .sorted(Comparator.comparing(Path::getFileName))
                    .toList();

            for (Path entry : entries)
            {
                Path curPath = entry.getFileName();
                if (Files.isDirectory(entry)) data.append(String.format("%-10s %s%n", "<DIR>", curPath));
                else data.append(String.format("%-10s %s%n", "", curPath));
            }

            os.setStatus(Status.OK);
            os.setMessage(data.toString());
        }
        catch (IOException e)
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Каталога " + curDir.toString() + " не существует");
        }

        return os;
    }

    public Path getParent()
    {
        return Paths.get(directoryName).getParent();
    }

    public OperationStatus cdParent()
    {
        OperationStatus os = new OperationStatus();

        Path parentDir = getParent();
        if (parentDir != null)
        {
            directoryName = parentDir.toString();
            os.setStatus(Status.OK);
            os.setMessage("");
        }
        else
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Родительский каталог отсутствует");
        }

        return os;
    }

    public OperationStatus cdChild(String childDirName)
    {
        OperationStatus os = new OperationStatus();

        Path childDir = Paths.get(directoryName).resolve(childDirName);
        if (childDirExists(childDir))
        {
            directoryName = childDir.toString();
            os.setStatus(Status.OK);
            os.setMessage(childDirName);
        }
        else
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Каталога " + childDirName + " не существует");
        }

        return os;
    }

    public boolean childDirExists(Path childDir)
    {
        return Files.exists(childDir) && Files.isDirectory(childDir);
    }

    public OperationStatus makeDir(String dirName)
    {
        OperationStatus os = new OperationStatus();

        Path curDir = Paths.get(directoryName);
        Path newDir = curDir.resolve(dirName);

        try
        {
            Files.createDirectory(newDir);
            os.setStatus(Status.OK);
            os.setMessage(newDir.toString());
        }
        catch (java.nio.file.FileAlreadyExistsException e)
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Каталог с данным именем уже существует");
        }
        catch (IOException e)
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Ошибка: " + e.getMessage());
        }

        return os;
    }

    public OperationStatus changeDir(String dirPath)
    {
        OperationStatus os = new OperationStatus();

        Path destDir = Paths.get(dirPath);
        if (childDirExists(destDir))
        {
            directoryName = destDir.toString();
            os.setStatus(Status.OK);
            os.setMessage("");
        }
        else
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Каталога " + dirPath + " не существует");
        }

        return os;
    }

    public OperationStatus deleteWithSubdir(String dirName) // полностью переделать
    {
        OperationStatus os = new OperationStatus();

        Path curDir = Paths.get(directoryName);
        Path toDelDir = curDir.resolve(dirName);

        if (childDirExists(toDelDir))
        {
            File toDelFile = toDelDir.toFile();
            deleteDirectory(toDelFile);
            os.setStatus(Status.OK);
            os.setMessage("Каталог удален успешно");
        }
        else
        {
            os.setStatus(Status.ERROR);
            os.setMessage("Каталога " + dirName + " не существует");
        }

        return os;
    }

    public static void deleteDirectory(File dir)
    {
        File[] contents = dir.listFiles();
        if (contents != null)
        {
            for (File file : contents)
            {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }

//    public void getFilesWithExtension(String ext)
//    {
//        Path curDir = Paths.get(directoryName);
//
//        try
//        {
//            Files.walk(curDir).forEach(file ->
//            {
//                File curFile = file.toFile();
//                if (curFile.isFile() && curFile.getPath().endsWith(ext))
//                {
//                    System.out.println(file.getFileName());
//                }
//            });
//        }
//        catch (IOException e)
//        {
//            System.err.println("Ошибка: " + e.getMessage());
//        }
//    }

    public OperationStatus getDirTree(String dirName, int level)
    {
        OperationStatus os = new OperationStatus();
        Path curDir = Paths.get(dirName);

        if (Files.exists(curDir) && Files.isDirectory(curDir))
        {
            String indent = level == 0 ? "" : " ".repeat(level) + "└" + "─".repeat(level);
            System.out.println(indent + curDir.getFileName());

            try
            {
                Files.list(curDir).filter(Files::isDirectory).forEach(path -> getDirTree(path.toString(), level + 1));
            }
            catch (IOException e)
            {
                os.setStatus(Status.ERROR);
                os.setMessage("Ошибка при выводе дерева каталогов");
            }
        }
        os.setStatus(Status.OK);
        os.setMessage("Операция выполнена успешно");
        return os;
    }

    public OperationStatus getDirTree(int level)
    {
        return getDirTree(directoryName, level);
    }

//    public String subDirExists(String curDirName, String subDirName)
//    {
//        Path curDir = Paths.get(curDirName);
//        if (Files.exists(curDir) && Files.isDirectory(curDir))
//        {
//            try
//            {
//                for (Path subDir : Files.list(curDir).filter(Files::isDirectory).toList())
//                {
//                    if (subDir.getFileName().toString().equals(subDirName)) return subDir.toString();
//                    String recursiveCheck = subDirExists(subDir.toString(), subDirName);
//                    if (!recursiveCheck.isEmpty()) return recursiveCheck;
//                }
//            }
//            catch (IOException e)
//            {
//                System.err.println("Ошибка при доступе к директории: " + e.getMessage());
//            }
//        }
//        return "";
//    }
//
//    public String subDirExists(String subDirName)
//    {
//        return subDirExists(directoryName, subDirName);
//    }
}