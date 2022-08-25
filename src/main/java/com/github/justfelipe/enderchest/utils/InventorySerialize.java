package com.github.justfelipe.enderchest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class InventorySerialize {

    public static JSONObject toJsonObject(Inventory inventory) {

        JSONObject object = new JSONObject();

        object.put("name", inventory.getName());
        object.put("size", inventory.getSize());


        JSONArray array = new JSONArray();

        for (int i = 0; i < inventory.getSize(); i++) {

            ItemStack item = inventory.getItem(i);

            if (item == null
                    || item.getType().equals(Material.AIR)) continue;

            String serialized = ItemSerialize.toBase64(item);
            JSONObject object1 = new JSONObject();

            object1.put(i, serialized);
            array.add(object1);

        }

        object.put("items", array);

        return object;

    }

    public static Inventory toInventory(String str){

        JSONObject object = (JSONObject) JSONValue.parse(str);

        return toInventory(object);

    }

    public static Inventory toInventory(JSONObject object) {

        String name = (String) object.get("name");
        Integer size = ((Long) object.get("size")).intValue();

        Inventory inventory = Bukkit.createInventory(null, size, name);

        JSONArray array = (JSONArray) object.get("items");

        array.forEach(o -> {

            JSONObject object1 = (JSONObject) o;

            object1.keySet().forEach(o1 -> {

                String key = (String) o1;
                Integer slot = Integer.parseInt(key);
                String serializedItem = (String) object1.get(o1);
                ItemStack item = ItemSerialize.fromBase64(serializedItem);

                inventory.setItem(slot, item);

            });
        });

        return inventory;
    }
}