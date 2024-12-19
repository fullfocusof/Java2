package org.example.Article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.google.gson.*;
import com.google.gson.GsonBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article
{
    String author, artName, preview;
    String[] tags;
    Date timeOfPublication;
    int views, readingTime;
    URL picURL;

    @Override
    public String toString()
    {
        return author + "\n" +
                artName + "\n" +
                timeOfPublication.toString() + "\n" +
                readingTime + " мин\n" +
                "Просмотры: " + views + "\n" +
                "Тэги: " + Arrays.toString(tags) + "\n" +
                "PicURL: " + picURL + "\n" +
                preview + "\n";
    }

    public static List<Article> getFromFile(String filename)
    {
        List<Article> arts = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename)))
        {
            while(sc.hasNext())
            {
                Article temp = new Article();
                temp.setAuthor(sc.nextLine());
                temp.setArtName(sc.nextLine());

                String dateInput = sc.nextLine();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, hh:mm");
                Date timeOfPub = new Date();
                try
                {
                    timeOfPub = formatter.parse(dateInput);
                }
                catch (ParseException e)
                {
                    System.out.println(e.getMessage());
                }
                temp.setTimeOfPublication(timeOfPub);

                temp.setReadingTime(Integer.parseInt(sc.next()));
                sc.nextLine();


                if (sc.hasNextInt())
                {
                    temp.setViews(sc.nextInt());
                    sc.nextLine();
                }
                else
                {
                    int views = -1, mp = 1;
                    String viewsInput = sc.nextLine();
                    if (viewsInput.endsWith("К"))
                    {
                        mp = 1000;
                        viewsInput = viewsInput.substring(0, viewsInput.length() - 1);
                        double value = Double.parseDouble(viewsInput);
                        views = (int) (value * mp);
                    }
                    temp.setViews(views);
                }

                String tagsInput = sc.nextLine();
                String[] tags = tagsInput.split(",\\s*");
                temp.setTags(tags);

                try
                {
                    URL url = new URL(sc.nextLine());
                    temp.setPicURL(url);
                }
                catch (MalformedURLException e)
                {
                    System.out.println(e.getMessage());
                }

                StringBuilder previewInput = new StringBuilder(sc.nextLine());
                while(sc.hasNextLine())
                {
                    String line = sc.nextLine();
                    if (line.trim().isEmpty()) break;
                    previewInput.append("\n").append(line);
                }
                temp.setPreview(previewInput.toString());

                arts.add(temp);
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
        }

        return arts;
    }

    public static void toGsonFile(List<Article> arts, String filename)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(filename))
        {
            gson.toJson(arts, writer);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void toJacksonFile(List<Article> arts, String filename)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.writeValue(new File(filename), arts);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

//        try (FileWriter writer = new FileWriter(filename))
//        {
//            mapper.writeValue(writer, arts);
////            for (int i = 0; i < arts.size(); i++)
////            {
////                String obj = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(arts.get(i));
////                mapper.writeValue(writer, obj);
////            }
//        }
//        catch (IOException e)
//        {
//            System.out.println(e.getMessage());
//        }
    }

    public static void toPDFFile(List<Article> arts, String filename)
    {
        Document doc = new Document();
        try
        {
            PdfWriter.getInstance(doc, new FileOutputStream(filename));
            doc.open();

            BaseFont bf = BaseFont.createFont("TimesNewRomanRegular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 14, Font.NORMAL, BaseColor.BLACK);
            Font URLfont = new Font(bf, 14, Font.UNDERLINE, BaseColor.BLUE);

            for (Article art : arts)
            {
                String data = art.getAuthor() + "\n" +
                        art.getArtName() + "\n" +
                        art.getTimeOfPublication().toString() + "\n" +
                        art.getReadingTime() + " мин\n" +
                        "Просмотры: " + art.getViews() + "\n" +
                        "Тэги: " + Arrays.toString(art.getTags()) + "\n" +
                        "PicURL: ";
                Paragraph paragraph = new Paragraph(data, font);
                doc.add(paragraph);

                String dataURL = art.getPicURL().toString();
                Chunk ch = new Chunk(dataURL, URLfont);
                ch.setAnchor(dataURL);
                doc.add(ch);

                String preview = art.getPreview() + "\n";
                paragraph = new Paragraph(preview, font);
                doc.add(paragraph);

                doc.add(new Paragraph("\n"));
            }
        }
        catch (DocumentException | IOException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            doc.close();
        }
    }

//    public static List<Article> getFromJsonFile(String filename)
//    {
//        List<Article> arts = new ArrayList<>();
//        Gson gson = new Gson();
//
//        try (FileReader reader = new FileReader(filename))
//        {
//            Article tempArt = gson.fromJson(reader, Article.class);;
//            arts.add(tempArt);
//        }
//        catch (IOException e)
//        {
//            System.out.println(e.getMessage());
//        }
//
//        return arts;
//    }
}