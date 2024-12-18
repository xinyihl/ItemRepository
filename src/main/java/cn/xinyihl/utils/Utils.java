package cn.xinyihl.utils;

import com.google.common.base.Charsets;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class Utils {

    public static String serializer(ItemStack itemStack) {
        String str = NBTItem.convertItemtoNBT(itemStack).toString();
        byte[] itemInput = str.getBytes(Charsets.UTF_8);
        return Base64.getEncoder().encodeToString(itemInput);
    }

    public static ItemStack deserializer(String string) {
        byte[] arrayOfByte = Base64.getDecoder().decode(string);
        String itemInput = new String(arrayOfByte, Charsets.UTF_8);
        return NBTItem.convertNBTtoItem(new NBTContainer(itemInput));
    }
}
