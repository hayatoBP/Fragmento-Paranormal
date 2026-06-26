package com.mycompany.fragmentoparanormal.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilitário centralizado para carregamento de imagens.
 *
 * Estratégia de carregamento (em ordem):
 *  1. Classpath via getResource().openStream()  — funciona após Maven build
 *  2. Classpath via getResourceAsStream()       — fallback alternativo
 *  3. Sistema de arquivos direto                — funciona no NetBeans sem build
 */
public class ImagemUtil {

    private static final String BASE_CLASSPATH = "/com/mycompany/fragmentoparanormal/images/";
    private static final String[] EXTENSOES = {".png", ".webp", ".jpg", ".jpeg"};

    // Detecta a raiz de recursos na inicialização
    private static final String RAIZ_DISCO = detectarRaizDisco();

    private static String detectarRaizDisco() {
        // Estratégia 1: a partir da localização das classes compiladas
        // Ex: target/classes  →  subir 2 níveis → raiz do projeto → src/main/resources
        try {
            URL loc = ImagemUtil.class.getProtectionDomain().getCodeSource().getLocation();
            if (loc != null) {
                File classesDir = new File(loc.toURI());
                // Sobe até encontrar pom.xml (raiz do projeto Maven)
                File dir = classesDir;
                for (int i = 0; i < 5; i++) {
                    File pom = new File(dir, "pom.xml");
                    if (pom.exists()) {
                        File resources = new File(dir, "src/main/resources");
                        if (resources.exists()) {
                            System.out.println("[ImagemUtil] Raiz encontrada via CodeSource: "
                                + resources.getAbsolutePath());
                            return resources.getAbsolutePath();
                        }
                    }
                    dir = dir.getParentFile();
                    if (dir == null) break;
                }
            }
        } catch (Exception ignored) {}

        // Estratégia 2: user.dir e seus pais
        String[] bases = {
            System.getProperty("user.dir"),
            System.getProperty("user.dir") + "/..",
            System.getProperty("user.dir") + "/../..",
        };
        for (String base : bases) {
            try {
                File dir = new File(base).getCanonicalFile();
                // Procura pom.xml neste diretório e nos filhos diretos
                if (new File(dir, "pom.xml").exists()) {
                    File resources = new File(dir, "src/main/resources");
                    if (resources.exists()) {
                        System.out.println("[ImagemUtil] Raiz encontrada via user.dir: "
                            + resources.getAbsolutePath());
                        return resources.getAbsolutePath();
                    }
                }
                // Procura em subpastas diretas (caso user.dir seja o workspace)
                File[] filhos = dir.listFiles(File::isDirectory);
                if (filhos != null) {
                    for (File filho : filhos) {
                        if (new File(filho, "pom.xml").exists()) {
                            File resources = new File(filho, "src/main/resources");
                            if (resources.exists()) {
                                System.out.println("[ImagemUtil] Raiz encontrada em subpasta: "
                                    + resources.getAbsolutePath());
                                return resources.getAbsolutePath();
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        System.err.println("[ImagemUtil] Raiz de recursos NÃO encontrada. user.dir="
            + System.getProperty("user.dir"));
        return null;
    }

    // ── API pública ───────────────────────────────────────────────────────

    /** Carrega imagem pelo caminho absoluto de classpath (com ou sem extensão). */
    public static Image carregar(String caminhoAbsoluto) {
        if (caminhoAbsoluto == null || caminhoAbsoluto.isBlank()) return null;

        if (temExtensao(caminhoAbsoluto)) {
            return carregarDireto(caminhoAbsoluto);
        }
        for (String ext : EXTENSOES) {
            Image img = carregarDireto(caminhoAbsoluto + ext);
            if (img != null) return img;
        }
        System.err.println("[ImagemUtil] Imagem não encontrada: " + caminhoAbsoluto);
        return null;
    }

    /** Personagem desarmado. Ex: "dominic" */
    public static Image carregarPersonagem(String nome) {
        return carregar(BASE_CLASSPATH + "personagens/" + nome);
    }

    /** Personagem armado, com fallback para desarmado. */
    public static Image carregarPersonagemArmado(String nome) {
        Image img = carregar(BASE_CLASSPATH + "personagens/" + nome + "_arma");
        return img != null ? img : carregarPersonagem(nome);
    }

    /** Imagem de diálogo, com fallback para desarmado. */
    public static Image carregarPersonagemDialogo(String nome) {
        Image img = carregar(BASE_CLASSPATH + "personagens/dialogos/" + nome);
        return img != null ? img : carregarPersonagem(nome);
    }

    /** Monstro/inimigo. Ex: "sangue_fraco" */
    public static Image carregarMonstro(String nome) {
        return carregar(BASE_CLASSPATH + "monstros/" + nome);
    }

    /** Cenário de batalha por elemento. */
    public static Image carregarCenarioBatalha(String elemento) {
        Image img = carregarDireto(BASE_CLASSPATH + "cenarios/" + elemento + "/batalha.png");
        if (img != null) return img;
        return carregar(BASE_CLASSPATH + "cenarios/cenario_" + elemento);
    }

    /** Cenário de boss por elemento. */
    public static Image carregarCenarioBoss(String elemento) {
        Image img = carregarDireto(BASE_CLASSPATH + "cenarios/" + elemento + "/boss.png");
        if (img != null) return img;
        return carregarCenarioBatalha(elemento);
    }

    /** Cenário numerado de missão (1-7). */
    public static Image carregarCenarioMissao(String elemento, int local) {
        Image img = carregarDireto(BASE_CLASSPATH + "cenarios/" + elemento + "/" + local + ".png");
        if (img != null) return img;
        return carregar(BASE_CLASSPATH + "cenarios/cenario_" + elemento);
    }

    /** Aplica imagem a um ImageView com null-safety. */
    public static void aplicar(ImageView iv, Image img) {
        if (iv != null && img != null) iv.setImage(img);
    }

    /** Carrega e aplica em um passo. */
    public static void carregarEAplicar(ImageView iv, String caminho) {
        aplicar(iv, carregar(caminho));
    }

    // ── Carregamento interno ──────────────────────────────────────────────

    private static Image carregarDireto(String caminho) {
        // 1ª tentativa: classpath via getResource()
        try {
            URL url = ImagemUtil.class.getResource(caminho);
            if (url != null) {
                try (InputStream s = url.openStream()) {
                    if (s != null) {
                        Image img = new Image(s);
                        if (!img.isError()) return img;
                    }
                }
            }
        } catch (Exception ignored) {}

        // 2ª tentativa: classpath via getResourceAsStream()
        try {
            InputStream s = ImagemUtil.class.getResourceAsStream(caminho);
            if (s != null) {
                Image img = new Image(s);
                s.close();
                if (!img.isError()) return img;
            }
        } catch (Exception ignored) {}

        // 3ª tentativa: disco direto (src/main/resources)
        if (RAIZ_DISCO != null) {
            try {
                // caminho é "/com/mycompany/..." — remove a barra inicial
                String relativo = caminho.startsWith("/") ? caminho.substring(1) : caminho;
                // Usa Paths.get para montar o caminho corretamente no Windows
                Path arquivo = Paths.get(RAIZ_DISCO, relativo.split("/"));
                File f = arquivo.toFile();
                System.out.println("[ImagemUtil] Tentando disco: " + f.getAbsolutePath() + " | existe=" + f.exists());
                if (f.exists()) {
                    try (FileInputStream fis = new FileInputStream(f)) {
                        Image img = new Image(fis);
                        if (!img.isError()) {
                            System.out.println("[ImagemUtil] Disco OK: " + f.getName());
                            return img;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[ImagemUtil] Erro disco: " + e.getMessage());
            }
        }

        return null;
    }

    private static boolean temExtensao(String caminho) {
        String lower = caminho.toLowerCase();
        for (String ext : EXTENSOES) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }
}
