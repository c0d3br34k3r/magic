package magic.misc;

import com.google.common.collect.ImmutableSet;

import magic.Card;
import magic.Type;
import magic.WholeCard;
import magic.Card.Builder;
import magic.CardPair;
import magic.Color;
import magic.Layout;
import magic.ManaCost;
import magic.ManaSymbol;

public class Test {

	public static void main(String[] args) {
		Builder fire = Card.builder().setName("Fire")
				.setManaCost(ManaCost.of(1, ManaSymbol.RED))
				.setTypes(ImmutableSet.of(Type.INSTANT))
				.setText("blah blah");

		Builder ice = Card.builder().setName("Ice")
				.setManaCost(ManaCost.of(1, ManaSymbol.BLUE))
				.setTypes(ImmutableSet.of(Type.INSTANT))
				.setText("blah blah");

		WholeCard whole = WholeCard.builder()
				.setColorIdentity(Color.parseSet("UR"))
				.setPair(CardPair.builder()
						.setFirst(fire)
						.setSecond(ice)
						.setLayout(Layout.SPLIT))
				.build();
		
		whole.print();
	}

}
