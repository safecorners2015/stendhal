/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.tavern.RareWeaponsSellerNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class McPeglegIOUTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new RareWeaponsSellerNPC().configureZone(zone, null);	

		AbstractQuest quest = new McPeglegIOU();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {	
		npc = SingletonRepository.getNPCList().get("McPegleg");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("I can't see that you got a valid IOU with my signature!", getReply(npc));
		en.step(player, "task");
		assertEquals("Perhaps if you find some #rare #armor or #weapon ...", getReply(npc));
		en.step(player, "rare armor");
		en.step(player, "weapon");
		assertEquals("Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer", getReply(npc));
		en.step(player, "rare armor");
		en.step(player, "McPegleg doesn't react although rare armor is blue...");
		en.step(player, "offer");
		assertEquals("Have a look at the blackboard on the wall to see my offers.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("I can't see that you got a valid IOU with my signature!", getReply(npc));
		en.step(player, "bye");

		assertEquals("I see you!", getReply(npc));
		// equip with IOU
		final Item note = SingletonRepository.getEntityManager().getItem("note");
		note.setDescription("IOU 250 money. (signed) McPegleg");
		note.setInfoString("charles");
		player.equipToInventoryOnly(note);
		en.step(player, "bye");

		en.step(player, "hi");
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("Where did you get that from? Anyways, here is the money *sighs*", getReply(npc));
		en.step(player, "got 100 money into my bag");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot));

		assertEquals("I see you!", getReply(npc));
		en.step(player, "hi");
		assertEquals("Yo matey! You look like you need #help.", getReply(npc));
		en.step(player, "IOU");
		assertEquals("You already got cash for that damned IOU!", getReply(npc));
		en.step(player, "bye");
	}
}
