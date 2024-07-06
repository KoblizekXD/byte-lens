module lol.koblizek.bytelens.core.decompiler.impl.vineflower {
    requires lol.koblizek.bytelens.core.decompiler.api;
    requires vineflower;

    provides lol.koblizek.bytelens.core.decompiler.api.Decompiler with lol.koblizek.bytelens.core.decompiler.impl.vineflower.VineflowerDecompiler;
}