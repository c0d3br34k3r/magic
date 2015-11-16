package magic.misc;

import com.google.common.collect.ImmutableSet;

import magic.Card;
import magic.Color;
import magic.Expansion;
import magic.Layout;
import magic.ManaCost;
import magic.Printing;
import magic.Type;
import magic.WholeCard;
import magic.WholePrinting;

public class Test {

	public static void main(String[] args) {

		WholeCard fireIce = WholeCard.builder().setLayout(Layout.SPLIT)
				.setColorIdentity(Color.parseSet("UR"))
				.setFirst(Card.builder().setName("Fire")
						.setManaCost(ManaCost.parse("{1}{R}"))
						.setTypes(ImmutableSet.of(Type.INSTANT))
						.setText("Fire..."))
				.setSecond(Card.builder().setName("Ice")
						.setManaCost(ManaCost.parse("{1}{U}"))
						.setTypes(ImmutableSet.of(Type.INSTANT))
						.setText("Ice..."))
				.build();

		WholePrinting.builder().setCard(fireIce)
				.setExpansion(Expansion.builder())
				.setFirst(Printing.builder())
				.setSecond(Printing.builder())
				.build().print();
	}

}
