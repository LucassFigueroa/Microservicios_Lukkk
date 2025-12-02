package minzbook.catalogservice.config;

import minzbook.catalogservice.model.Book;
import minzbook.catalogservice.repository.BookRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Component
public class BookDataLoader implements org.springframework.boot.CommandLineRunner {

    private final BookRepository bookRepository;

    public BookDataLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Evitar duplicados si ya hay datos
        if (bookRepository.count() > 0) {
            return;
        }

        System.out.println("📚 Precargando libros iniciales con portadas BLOB (redimensionadas)...");

        crearLibro(
                "Informática en 8 pasos",
                "Skadi",
                "Aprende informática desde cero.",
                10000.0,
                10,
                "Tecnología",
                "portadas/portada1.jpg"
        );

        crearLibro(
                "Poder Tecnológico",
                "Heavy",
                "La tecnología es poder.",
                15000.0,
                12,
                "Tecnología",
                "portadas/portada2.jpg"
        );

        crearLibro(
                "Aprende a codear",
                "Crown",
                "El arte de la programación.",
                20000.0,
                8,
                "Programación",
                "portadas/portada3.jpg"
        );

        crearLibro(
                "Java sí, Python no",
                "Victor",
                "¿Java o Python? El eterno debate.",
                25000.0,
                6,
                "Programación",
                "portadas/portada4.jpg"
        );

        crearLibro(
                "Lolsito: la odisea",
                "Lukkk",
                "Una aventura épica en el mundo de los videojuegos.",
                30000.0,
                5,
                "Videojuegos",
                "portadas/portada5.jpg"
        );
    }

    private void crearLibro(
            String titulo,
            String autor,
            String descripcion,
            Double precio,
            Integer stock,
            String categoria,
            String resourcePath
    ) throws IOException {

        ClassPathResource imgResource = new ClassPathResource(resourcePath);

        if (!imgResource.exists()) {
            System.out.println("⚠ No se encontró la imagen: " + resourcePath);
            return;
        }

        // 1) Leer imagen original
        BufferedImage original = ImageIO.read(imgResource.getInputStream());
        if (original == null) {
            System.out.println("⚠ No se pudo leer la imagen (formato desconocido): " + resourcePath);
            return;
        }

        // 2) Redimensionar manteniendo aspecto (ancho máximo 600 px)
        int maxWidth = 600;
        int width = original.getWidth();
        int height = original.getHeight();

        double scale = 1.0;
        if (width > maxWidth) {
            scale = (double) maxWidth / (double) width;
        }

        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // 3) Comprimir a JPEG con calidad ~0.75
        byte[] portadaBytes = compressToJpeg(resized, 0.75f);

        System.out.println("✔ " + titulo + " portada comprimida: " + portadaBytes.length + " bytes");

        Book book = new Book();
        book.setTitulo(titulo);
        book.setAutor(autor);
        book.setDescripcion(descripcion);
        book.setPrecio(precio);
        book.setStock(stock);
        book.setCategoria(categoria);
        book.setActivo(true);

        book.setPortada(portadaBytes);
        book.setPortadaContentType("image/jpeg");

        bookRepository.save(book);

        System.out.println("✔ Libro precargado: " + titulo);
    }

    private byte[] compressToJpeg(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No ImageWriter para JPG encontrado");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); // 0.0 (peor) a 1.0 (mejor)
        }

        try (MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(baos)) {
            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }
}
