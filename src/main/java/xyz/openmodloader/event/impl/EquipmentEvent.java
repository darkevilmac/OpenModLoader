package xyz.openmodloader.event.impl;

import java.util.Objects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.Event;

/**
 * Parent class for equipment related events. All events that fall within this
 * scope should extend this class. They should usually also be added as
 * inner classes, although this is not an absolute requirement.
 */
public abstract class EquipmentEvent extends Event {

    /**
     * The entity that has fired this event
     */
    protected final Entity entity;

    /**
     * The item that was used
     */
    protected ItemStack equipment;

    /**
     * The equipment slot that was used.
     */
    protected EntityEquipmentSlot slot;

    /**
     * Constructor for the base equipment events. This constructor should only be
     * accessed through super calls.
     *
     * @param entity The entity that has fired this event.
     * @param equipment The equipment that was used
     * @param slot The equipment slot that was used.
     */
    public EquipmentEvent(Entity entity, ItemStack equipment, EntityEquipmentSlot slot) {
        this.entity = entity;
        this.equipment = equipment;
        this.slot = slot;
    }

    /**
     * Gets the piece of equipment that will be used.
     * 
     * @return the piece of equipment that will be used.
     */
    public ItemStack getEquipment() {
        return equipment;
    }

    /**
     * Gets the entity that this event was fired for.
     * 
     * @return the entity that fired the event.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the slot that will be used.
     * 
     * @return the slot that will be used.
     */
    public EntityEquipmentSlot getSlot() {
        return slot;
    }

    /**
     * Sets the piece of equipment to be used.
     *
     * @param equipment The piece of equipment that should be used.
     */
    public void setEquipment(ItemStack equipment) {
        this.equipment = Objects.requireNonNull(equipment);
    }

    /**
     * Sets the slot to equip this equipment in.
     * 
     * @param slot the slot
     */
    public void setSlot(EntityEquipmentSlot slot) {
        this.slot = Objects.requireNonNull(slot);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    /**
     * Fired whenever an equipment is added to an entity.
     * 
     * Fired from {@link EntityLiving#setItemStackToSlot(EntityEquipmentSlot, ItemStack)},
     * {@link EntityArmorStand#setItemStackToSlot(EntityEquipmentSlot, ItemStack)},
     * {@link EntityPlayer#setItemStackToSlot(EntityEquipmentSlot, ItemStack)},
     * {@link ContainerPlayer#transferStackInSlot(EntityPlayer, int)}.
     */
    public static class Equip extends EquipmentEvent {

        /**
         * Constructor for this Equip event which is fired when a piece of equipment
         * is equipped.
         *
         * @param entity The entity that equipped the equipment
         * @param equipment The equipment that was equipped
         * @param slot The equipment slot that was equipped
         */
        public Equip(Entity entity, ItemStack equipment, EntityEquipmentSlot slot) {
            super(entity, equipment, slot);
        }

        public static EquipmentEvent handle(Entity entity, ItemStack equipment, EntityEquipmentSlot slot) {
            EquipmentEvent event;
            if (equipment != null) {
                event = new EquipmentEvent.Equip(entity, equipment, slot);
            } else {
                event = new EquipmentEvent.Unequip(entity, equipment, slot);
            }
            return OpenModLoader.getEventBus().post(event) ? event : null;
        }
    }

    /**
     * Fired whenever an equipment is removed from an entity.
     * 
     * Fired from {@link EntityLiving#setItemStackToSlot(EntityEquipmentSlot, ItemStack)},
     * {@link EntityArmorStand#setItemStackToSlot(EntityEquipmentSlot, ItemStack)},
     * {@link EntityPlayer#setItemStackToSlot(EntityEquipmentSlot, ItemStack)},
     * {@link ContainerPlayer#transferStackInSlot(EntityPlayer, int)}.
     */
    public static class Unequip extends EquipmentEvent {

        /**
         * Constructor for this Equip event which is fired when a piece of equipment
         * is unequipped.
         *
         * @param entity The entity that unequipped the equipment
         * @param equipment The equipment that was unequipped
         * @param slot The equipment slot that was unequipped
         */
        public Unequip(Entity entity, ItemStack item, EntityEquipmentSlot slot) {
            super(entity, item, slot);
        }
    }
}
