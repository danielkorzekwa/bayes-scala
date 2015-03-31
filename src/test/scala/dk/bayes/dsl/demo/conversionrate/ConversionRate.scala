package dk.bayes.dsl.demo.conversionrate

/**
 * @param conversions Numbers of times for a visitor to buy something given he clicked and landed on the product page.
 */
case class ConversionRate(item:Item,clicks:Int,conversions:Int)