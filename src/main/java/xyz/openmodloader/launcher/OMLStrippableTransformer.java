package xyz.openmodloader.launcher;

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Sets;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.launcher.strippable.*;

public class OMLStrippableTransformer implements IClassTransformer {
    static Set<String> MODS;
    static Side SIDE;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        checkClass(classNode);

        if (removeInterfaces(classNode) ||
                classNode.methods.removeIf(method -> remove(method.visibleAnnotations)) ||
                classNode.fields.removeIf(fields -> remove(fields.visibleAnnotations))) {
            OpenModLoader.getLogger().debug("Stripping elements from class '%s'", classNode.name);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        return basicClass;
    }

    private boolean removeInterfaces(ClassNode classNode) {
        boolean b = false;
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an: classNode.visibleAnnotations) {
                Type anType = Type.getType(an.desc);
                if (anType.equals(Type.getType(Strippable.Interface.class))) {
                    b = b | handleStrippableInterface(classNode, an);
                } else if (anType.equals(Type.getType(InterfaceContainer.class))) {
                    for (Object o: (Iterable<?>) an.values.get(1)) {
                        b = b | handleStrippableInterface(classNode, (AnnotationNode) o);
                    }
                }
            }
        }
        return b;
    }

    private boolean handleStrippableInterface(ClassNode classNode, AnnotationNode an) {
        List<Object> values = an.values;
        String side = Side.UNIVERSAL.name();
        String envo = Environment.UNIVERSAL.name();
        List<String> mods = Collections.emptyList();
        List<String> classes = Collections.emptyList();
        final Set<String> interfaces = Sets.newHashSet();
        for (int i = 0; i < values.size() - 1; i += 2) {
            Object key = values.get(i);
            Object value = values.get(i + 1);
            if (key instanceof String && ((String) key).equals("mods")) {
                mods = (List<String>) value;
            } else if (key instanceof String && ((String) key).equals("classes")) {
                classes = (List<String>) value;
            } else if (key instanceof String && ((String) key).equals("side")) {
                side = ((String[]) value)[1];
            } else if (key instanceof String && ((String) key).equals("environment")) {
                envo = ((String[]) value)[1];
            } else if (key instanceof String && ((String) key).equals("interfaces")) {
                interfaces.addAll((List<String>) value);
            }
        }
        if (!side.equals("UNIVERSAL") && !SIDE.toString().equals(side)) {
            return classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
        }
        if (!envo.equals("UNIVERSAL") && !getEnvironment().toString().equals(side)) {
            return classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
        }
        for (String mod: mods) {
            if (!MODS.contains(mod)) {
                return classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
            }
        }
        for (String cls: classes) {
            try {
                Class.forName(cls, false, Launch.classLoader);
            } catch (ClassNotFoundException e) {
                return classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
            }
        }
        return false;
    }

    public boolean remove(List<AnnotationNode> annotations) {
        if (annotations != null) {
            for (AnnotationNode annotation : annotations) {
                if (Type.getType(annotation.desc).equals(Type.getType(Strippable.class))) {
                    List<Object> values = annotation.values;
                    for (int i = 0; i < values.size() - 1; i += 2) {
                        Object key = values.get(i);
                        Object value = values.get(i + 1);
                        if (key instanceof String && ((String) key).equals("side")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                if (!side.equals("UNIVERSAL") && !side.equals(SIDE.toString())) {
                                    return true;
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("environment")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                if (!side.equals("UNIVERSAL") && !side.equals(getEnvironment().toString())) {
                                    return true;
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("mods")) {
                            if (value instanceof List) {
                                List<?> mods = (List<?>) value;
                                for (Object mod: mods) {
                                    if (!MODS.contains(mod)) {
                                        return true;
                                    }
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("classes")) {
                            if (value instanceof List) {
                                List<?> classes = (List<?>) value;
                                for (Object cls: classes) {
                                    try {
                                        Class.forName((String) cls, false, Launch.classLoader);
                                    } catch (ClassNotFoundException e) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void checkClass(ClassNode classNode) {
        List<AnnotationNode> annotations = classNode.visibleAnnotations;
        if (annotations != null) {
            for (AnnotationNode annotation : annotations) {
                if (Type.getType(annotation.desc).equals(Type.getType(Strippable.class))) {
                    List<Object> values = annotation.values;
                    for (int i = 0; i < values.size() - 1; i += 2) {
                        Object key = values.get(i);
                        Object value = values.get(i + 1);
                        if (key instanceof String && ((String) key).equals("side")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                if (!side.equals("UNIVERSAL") && !side.equals(SIDE.toString())) {
                                    throw new RuntimeException(
                                            String.format("Loading class %s on wrong side %s", classNode.name, SIDE));
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("environment")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                if (!side.equals("UNIVERSAL") && !side.equals(getEnvironment().toString())) {
                                    throw new RuntimeException(String.format("Loading class %s in wrong environment %s",
                                            classNode.name, environment));
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("mods")) {
                            if (value instanceof List) {
                                List<?> mods = (List<?>) value;
                                List<Object> missingMods = new ArrayList<>();
                                for (Object mod : mods) {
                                    if (!MODS.contains(mod)) {
                                        missingMods.add(mod);
                                    }
                                }
                                if (!missingMods.isEmpty()) {
                                    throw new RuntimeException(
                                            String.format("Loading class %s without required mods %s loaded",
                                                    classNode.name, missingMods));
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("classes")) {
                            if (value instanceof List) {
                                List<?> classes = (List<?>) value;
                                List<Object> missingClasses = new ArrayList<>();
                                for (Object cls : classes) {
                                    try {
                                        Class.forName((String) cls, false, Launch.classLoader);
                                    } catch (ClassNotFoundException e) {
                                        missingClasses.add(cls);
                                    }
                                }
                                if (!missingClasses.isEmpty()) {
                                    throw new RuntimeException(String.format(
                                            "Loading class %s without required classes %s on the classpath",
                                            classNode.name, missingClasses));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static Environment environment;

    public static Environment getEnvironment() {
        if (environment == null) {
            try {
                Class.forName("net.minecraft.block.Block", false, Launch.classLoader);
                environment = Environment.DEVELOPMENT;
            } catch (ClassNotFoundException e) {
                environment = Environment.PRODUCTION;
            }
        }
        return environment;
    }

    public static Side getSide() {
        return SIDE;
    }
}
