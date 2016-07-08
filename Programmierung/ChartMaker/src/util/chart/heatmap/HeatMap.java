/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.chart.heatmap;

import java.awt.BasicStroke;
import util.chart.heatmap.color.HeatMapColorScale;
import util.chart.heatmap.color.BlueHeatMapColorScale;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import util.chart.heatmap.color.RedHeatMapColorScale;

/**
 *
 * @author Philipp
 */
public class HeatMap<I, C>  {
    
    protected HeatMapDataSet<I, C> data;
    protected HashMap<I, BufferedImage> idImages;
    protected HashMap<C, BufferedImage> catImages;
    protected HashMap<C, Double> maxValues;
    protected double totalMaxVal;
    protected boolean useOneScale = false;
    protected HeatMapColorScale scale;

    public HeatMap(HeatMapDataSet ds) {
        this.data = ds;
        idImages = new HashMap<I, BufferedImage>();
        catImages = new HashMap<C, BufferedImage>();
        maxValues = new HashMap<>();
        calculateMaxValues();
        this.scale = new BlueHeatMapColorScale();
        totalMaxVal = 0;
    }

    public void setColorScale(HeatMapColorScale scale) {
        this.scale = scale;
    }

    public void setOneScale(boolean b) {
        this.useOneScale = b;
    }

    public void calculateMaxValues() {
        for (C cat : data.getCategories()) {
            double maxVal = 0.;
            for (I id : data.getIdentifiers()) {
                if (data.getValue(id, cat) > maxVal) {
                    maxVal = data.getValue(id, cat);
                    if(maxVal > totalMaxVal){
                        totalMaxVal = maxVal;
                    }
                }
            }
            maxValues.put(cat, maxVal);
        }
    }

    public void draw(Graphics2D gd, Rectangle2D rd) {
        calculateIdImages(gd);
        calculateCatImages(gd);
        int idW = getIdWidth();
        int idH = getIdHeigth();
        int catW = getCatWidth();
        int catH = getCatHeight();
        int absH = catH + idH * data.getIdentifiers().size();
        int absW = idW + catW * data.getCategories().size();
        int i = 0;
        for (C cat : data.getCategories()) {
            BufferedImage img = catImages.get(cat);
            gd.drawImage(img, idW + i * catW, 0, null);
            
            i++;
        }
        i = 0;
        for (I id : data.getIdentifiers()) {
            BufferedImage img = idImages.get(id);
            gd.drawImage(img, 0, catH + i * idH, null);
            i++;
        }
        i = 0;
        for (I id : data.getIdentifiers()) {
            int j = 0;
            for (C cat : data.getCategories()) {
                if (useOneScale) {
                    Rectangle2D rect = new Rectangle(idW + j * catW, catH + i * idH, catW , idH );
                    gd.setColor(scale.getColor(data.getValue(id, cat), totalMaxVal));    
                    gd.fill(rect);
                    gd.setColor(Color.WHITE);
                    gd.drawRect(idW + j * catW, catH + i * idH, catW , idH );
                    j++;
                } else {
//                    System.out.println("value:"+data.getValue(id, cat));
//                    System.out.println("maxVal: "+maxValues.get(cat));
                    Rectangle2D rect = new Rectangle(idW + j * catW, catH + i * idH, catW , idH );
                    gd.setColor(scale.getColor(data.getValue(id, cat), maxValues.get(cat)));    
                    gd.fill(rect);
                    gd.setColor(Color.WHITE);
                    gd.draw(rect);
                    j++;
                }
            }
            i++;
        }
    }
    
    protected void calculateIdImages(Graphics2D gd) {
        HashMap<I, BufferedImage> map = new HashMap<>();
        for (I id : data.getIdentifiers()) {
            map.put(id, createStringImage(gd, id.toString()));
        }
        this.idImages = map;
    }
    
    protected void calculateCatImages(Graphics2D gd) {
        HashMap<C, BufferedImage> map = new HashMap<C, BufferedImage>();
        int i = 0;
        File folder = new File("/home/bode/Desktop/");
        for (C cat : data.getCategories()) {
            BufferedImage img = createStringImage(gd, cat.toString());
            File heatfilebucket = new File(folder, ""+i+".png");
            try {
                ImageIO.write(img, "png", heatfilebucket);
            } catch (IOException ex) {
                Logger.getLogger(HeatMap.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedImage img2 = new BufferedImage(img.getHeight(), img.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgg2d = img2.createGraphics();
            AffineTransform aff = new AffineTransform();
            aff.rotate(Math.toRadians(-90));
            aff.translate(-1 * img.getWidth(), 0);
//            aff.translate(-1 * img.getWidth(), img.getWidth() - img.getHeight() * 2 + (2.5));
            imgg2d.setColor(Color.WHITE);
            imgg2d.fillRect(0, 0, img2.getWidth(), img2.getHeight());
            imgg2d.setColor(gd.getColor());
            imgg2d.setBackground(gd.getBackground());
            imgg2d.drawImage(img, aff, null);
            File heatfilebucket2 = new File(folder, ""+i+"rot.png");
            try {
                ImageIO.write(img2, "png", heatfilebucket2);
            } catch (IOException ex) {
                Logger.getLogger(HeatMap.class.getName()).log(Level.SEVERE, null, ex);
            }
            map.put(cat, img2);
            i++;
        }
        this.catImages = map;
    }
    
    public static BufferedImage createStringImage(Graphics g, String s) {
        int w = g.getFontMetrics().stringWidth(s) + 4;
        int h = g.getFontMetrics().getHeight();
        
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = image.createGraphics();
//        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        imageGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        imageGraphics.setColor(Color.WHITE);
        imageGraphics.fillRect(0, 0, w, h);
        imageGraphics.setColor(Color.BLACK);
        imageGraphics.setFont(g.getFont());
        imageGraphics.drawString(s, 2, h - g.getFontMetrics().getDescent());
        imageGraphics.dispose();
        
        return image;
    }
    
    public static void main(String[] args) {
        HeatMapDataSet<String, String> ds = new SortableHeatMapDataSet<>();
        ds.add("balala", "cat1", 5.);
        ds.add("balala", "cat2", 3.);
        ds.add("balala", "cat3", 2.);
        ds.add("id2", "cat1", 2.);
        ds.add("id2", "cat2", 0.);
        ds.add("id2", "cat3", 8.);
        ds.add("id3", "cat1", 5.);
        ds.add("id3", "cat2", 7.);
        ds.add("id3", "cat3", 2.);
        HeatMap hm = new HeatMap(ds);
        hm.setColorScale(new RedHeatMapColorScale());
        BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        Rectangle rec = new Rectangle(0,0,500,500);
        g2d.setColor(Color.WHITE);
        g2d.draw(rec);
        g2d.fill(rec);
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.black);
        hm.draw(g2d, null);
        File outputfile = new File("/home/bode/Desktop/heatmap.png");
        try {
            ImageIO.write(img, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(HeatMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int getIdWidth() {
        int idW = 0;
        for (I id : data.getIdentifiers()) {
            BufferedImage idImg = idImages.get(id);
            int w = idImg.getWidth();
            if (w > idW) {
                idW = w;
            }
        }
        return idW;
    }

    private int getIdHeigth() {
        int idH = 0;
        for (I id : data.getIdentifiers()) {
            BufferedImage idImg = idImages.get(id);
            int h = idImg.getHeight();
            if (h > idH) {
                idH = h;
            }
        }
        return idH;
    }

    private int getCatWidth() {
        int catW = 0;
        for (C cat : data.getCategories()) {
            BufferedImage catImg = catImages.get(cat);
            int w = catImg.getWidth();
            if (w > catW) {
                catW = w;
            }
        }
        return catW;
    }

    private int getCatHeight() {
        int catH = 0;
        for (C cat : data.getCategories()) {
            BufferedImage catImg = catImages.get(cat);
            int h = catImg.getHeight();
            if (h > catH) {
                catH = h;
            }
        }
        return catH;
    }
}
