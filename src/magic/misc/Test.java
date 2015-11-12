package magic.misc;

import java.util.EnumSet;

import magic.Card;
import magic.Color;
import magic.Layout;
import magic.ManaCost;
import magic.Type;
import magic.WholeCard;

public class Test {

	public static void main(String[] args) {

		WholeCard whole = WholeCard.builder()
				.setFirst(Card.builder()
						.setName("Fire")
						.setManaCost(ManaCost.parse("{1}{R}"))
						.setTypes(EnumSet.of(Type.INSTANT))
						.setText(
								"Fire deals 2 damage divided as you choose among any number of target creatures and/or players."))

		.setSecond(Card.builder()
				.setName("Ice")
				.setManaCost(ManaCost.parse("{1}{U}"))
				.setTypes(EnumSet.of(Type.INSTANT))
				.setText("Tap target creature.\nDraw a card."))
//				.setLayout(Layout.SPLIT)
				.setColorIdentity(Color.parseSet("UR")).build();
		whole.print();

	}

}
