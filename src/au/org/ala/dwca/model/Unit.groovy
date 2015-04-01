package au.org.ala.dwca.model

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class Unit extends Term {
    /** The unit symbol */
    String symbol
    /** The in-unit string to use for the unit */
    String descriptive

    /**
     * Construct from a configuration
     *
     * @param config
     */
    Unit(config) {
        super(config)
        if (symbol) aliases.add(symbol)
        if (!descriptive) descriptive = name.replaceAll("[^a-zA-Z0-9]", " ")
    }

    /**
     * Build a unit from a configuration.
     *
     * @param config The configutation
     */
    @Override
    def fromConfig(Object config) {
        super.fromConfig(config)
        symbol = config.symbol
        descriptive = config.descriptive
    }

    /**
     * Update from another unit.
     *
     * @param other The other unit
     */
    @Override
    def updateFrom(Named other) {
        super.updateFrom(other)
        if (other instanceof Unit) {
            Unit ou = (Unit) other
            if (!symbol) symbol = ou.symbol
            if (!descriptive) descriptive = ou.descriptive
        }
    }
}
