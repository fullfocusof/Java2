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
import java.util.stream.Collectors;

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

    public void getContents()
    {
        Path curDir = Paths.get(directoryName);

        try
        {
            List<Path> entries = Files.list(curDir)
                    .sorted(Comparator.comparing(Path::getFileName))
                    .collect(Collectors.toList());

            for (Path entry : entries)
            {
                Path curPath = entry.getFileName();
                if (Files.isDirectory(entry)) System.out.println("<DIR>\t" + curPath);
                else System.out.println("\t\t" + curPath);
            }

//            os.setStatus(Status.OK);
//            os.setMessage(data.toString());
        }
        catch (IOException e)
        {
//            os.setStatus(Status.ERROR);
//            os.setMessage("Каталога " + curDir.toString() + " не существует");
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public Path getParent()
    {
        return Paths.get(directoryName).getParent();
    }

    public void cdParent()
    {
        Path parentDir = getParent();
        if (parentDir != null) directoryName = parentDir.toString();
    }

    public void cdChild(String childDirName, OperationStatus os)
    {
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
    }

    public boolean childDirExists(Path childDir)
    {
        return Files.exists(childDir) && Files.isDirectory(childDir);
    }

    public void makeDir(String dirName)
    {
        Path curDir = Paths.get(directoryName);
        Path newDir = curDir.resolve(dirName);

        try
        {
            Files.createDirectory(newDir);
        }
        catch (java.nio.file.FileAlreadyExistsException e)
        {
            System.err.println("Каталог с данным именем уже существует");
        }
        catch (IOException e)
        {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public void changeDir()
    {

    }

    public void deleteAllSubdir()
    {
        Path curDir = Paths.get(directoryName);

        try
        {
            Files.walk(curDir).filter(path -> !path.equals(curDir) && Files.isDirectory(path)).forEach(path ->
            {
                try
                {
                    Files.delete(path);
                }
                catch (IOException e)
                {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            });
        }
        catch (IOException e)
        {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public void getFilesWithExtension(String ext)
    {
        Path curDir = Paths.get(directoryName);

        try
        {
            Files.walk(curDir).forEach(file ->
            {
                File curFile = file.toFile();
                if (curFile.isFile() && curFile.getPath().endsWith(ext))
                {
                    System.out.println(file.getFileName());
                }
            });
        }
        catch (IOException e)
        {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public void getDirTree(String dirName, int level)
    {
        Path curDir = Paths.get(dirName);

        if (Files.exists(curDir) && Files.isDirectory(curDir))
        {
            String indent = level == 0 ? " " : " ".repeat(level) + "﹂";
            System.out.println(indent + curDir.getFileName() + "/");

            try
            {
                Files.list(curDir).filter(Files::isDirectory).forEach(path -> getDirTree(path.toString(), level + 1));
            }
            catch (IOException e)
            {
                System.err.println("Ошибка при доступе к директории: " + e.getMessage());
            }
        }
    }

    public void getDirTree(int level)
    {
        getDirTree(directoryName, level);
    }

    public String subDirExists(String curDirName, String subDirName)
    {
        Path curDir = Paths.get(curDirName);
        if (Files.exists(curDir) && Files.isDirectory(curDir))
        {
            try
            {
                for (Path subDir : Files.list(curDir).filter(Files::isDirectory).toList())
                {
                    if (subDir.getFileName().toString().equals(subDirName)) return subDir.toString();
                    String recursiveCheck = subDirExists(subDir.toString(), subDirName);
                    if (!recursiveCheck.isEmpty()) return recursiveCheck;
                }
            }
            catch (IOException e)
            {
                System.err.println("Ошибка при доступе к директории: " + e.getMessage());
            }
        }
        return "";
    }

    public String subDirExists(String subDirName)
    {
        return subDirExists(directoryName, subDirName);
    }
}