//? if <=1.12.2 {
/*package io.github.archipelagominecraft.core.loaders.legacy;

import com.google.common.collect.ImmutableList;
import io.github.archipelagominecraft.core.ArchipelagoClientConstants;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// For MixinBooter

public class LegacyForgeCorePlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of(ArchipelagoClientConstants.MOD_MIXINS_FILE + ".mixins.json");
    }
}

*///?}
