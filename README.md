# Gerber & Excellon processing library
---
This library allows to read and write Gerber and Excellon DRL files 
as well as render them into BufferedImage.

Pending features:
 - merging multiple Gerber batches (all layers) into one (nesting)
 - adding mouse-bites features into merged Gerber

## Short usage how-to:

Read Gerber file:
```java
var reader = new GerberReader(new FileInputStream(fileName));
var gerber = reader.read("Copper Back Title");
```

Read Excellon file:
```java
var drlReader = new ExcellonReader(new FileInputStream(fileName));
var drl = drlReader.read("Top through holes");
```

Render Gerber layer with drilled holes into PNG:
```java
void render() {
    var scale = 0.02; // One millimeter is 50px
    var renderer = new BufferedImageRenderer(scale);
    renderer.render(gerber, Color.ORANGE);
    renderer.render(drl, Color.WHITE);

    var image = renderer.getImage();
    ImageIO.write(image, "png", new File("rendered.png"));
}
```

### Disclaimer
This software is in development, use it on your own risk.