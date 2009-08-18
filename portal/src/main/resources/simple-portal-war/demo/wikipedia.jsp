<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page language="java" %>
<%@ taglib prefix="xportal" tagdir="/WEB-INF/tags/portal" %>
<%@ taglib uri="/WEB-INF/portal.tld" prefix="portal" %>
<%@ page isELIgnored="false" %>

<%@ include file="/layouts/header.jsp" %>

<portal:page>
   <portal:pageparam namespaceURI="urn:jboss:portal:simple:google" localName="zipcode" value="32080"/>
   <div class="wiki-content full-width">
      <div class="two-third-width float-right">
         <xportal:portlet name="GoogleMap" applicationName="samples-google-portlet"/>
      </div>
      <h2 class="title">St. Augustine, Florida</h2>

      <p>St. Augustine is the county seat of St. Johns County, Florida, in the United States. It is the
         oldest continuously occupied European-established city, and the oldest port in the continental United
         States. St. Augustine lies in a region of Florida known as The First Coast, which extends from
         Amelia Island in the north, south to Jacksonville, St. Augustine and Palm Coast. According to the 2000
         census, the city population was 11,592; in 2004, the population estimated by the U.S. Census Bureau was
         12,157.</p>

      <p>St. Augustine was founded by the Spanish in 1565. The first Christian worship service held in a
         permanent settlement in the continental United States was a Catholic Mass celebrated in St. Augustine.
         A few settlements were founded prior to St. Augustine but all failed, including the original Pensacola
         colony in West Florida, founded by Tristán de Luna y Arellano in 1559, with the area abandoned in 1561
         due to hurricanes, famine and warring tribes, and Fort Caroline in what is today Jacksonville, Florida,
         in 1564. The city was founded by the Spanish admiral Pedro Menéndez de Avilés on September 8, 1565.
         Menéndez first sighted land on August 28, the feast day of Augustine of Hippo, and consequently named
         the settlement San Agustín. Martín de Argüelles was born here one year later in 1566, the first child of
         European ancestry to be born in what is now the continental United States. This came 21 years before the
         English settlement at Roanoke Island in Virginia Colony, and 42 years before the successful settlements
         of Santa Fe, New Mexico, and Jamestown, Virginia. In all the territory under the jurisdiction of the
         United States, only (European) settlements in Puerto Rico are older than St. Augustine, with the oldest
         being Caparra, founded in 1508, whose inhabitants relocated and founded San Juan, in 1521.</p>

      <p>In 1586 St. Augustine was attacked and burned by Sir Francis Drake. In 1668 it was plundered by pirates
         and most of the inhabitants were killed. In 1702 and 1740 it was unsuccessfully attacked by British
         forces from their new colonies in the Carolinas and Georgia. The most serious of these came in the
         latter year, when James Oglethorpe of Georgia allied himself with Ahaya the Cowkeeper, chief of the
         Alachua band of the Seminole tribe to lay siege to the city.</p>

      <p>In 1763, the Treaty of Paris ended the French and Indian War and gave Florida and St. Augustine to the
         British, an acquisition the British had been unable to take by force and keep due to the strong fort
         there. St. Augustine came under British rule and served as a Loyalist colony during the American
         Revolutionary War. The Treaty of Paris in 1783 gave the American colonies north of Florida their
         independence, and ceded Florida to Spain in recognition of Spanish efforts on behalf of the American
         colonies during the war.</p>

      <p>Florida was under Spanish control again from 1784 to 1821. During this time, Spain was being invaded by
         Napoleon and was struggling to retain its colonies. Florida no longer held its past importance to Spain.
         The expanding United States, however, regarded Florida as vital to its interests. In 1821, the
         Adams-Onís Treaty peaceably turned the Spanish colonies in Florida and, with them, St. Augustine, over
         to the United States.</p>

      <p>Florida was a United States territory until 1845 when it became a U.S. state. In 1861, the American
         Civil War began and Florida seceded from the Union and joined the Confederacy. Days before Florida
         seceded, state troops took the fort at St. Augustine from a small Union garrison (January 7, 1861).
         However, federal troops loyal to the United States Government quickly reoccupied the city (March 11,
         1862) and remained in control throughout the four-year-long war. In 1865, Florida rejoined the United
         States.</p>

      <p>Spanish Colonial era buildings still existing in the city include the fortress Castillo de San Marcos.
         The fortress successfully repelled the British attacks of the 18th century, served as a prison for the
         Native American leader Osceola in 1837, and was occupied by Union troops during the American Civil War.
         It was removed from the Army's active duty rolls in 1900 after 205 years of service under five different
         flags. It is now the Castillo de San Marcos National Monument.</p>

      <p>In the late 19th century the railroad came to town, and led by northeastern industrialist Henry Flagler,
         St. Augustine became a winter resort for the very wealthy. A number of mansions and palatial grand
         hotels of this era still exist, some converted to other use, such as housing parts of Flagler College
         and museums. Flagler went on to develop much more of Florida's east coast, including his Florida East
         Coast Railway which eventually reached Key West in 1912.</p>

      <p>The city is a popular tourist attraction, for the rich Spanish Colonial Revival Style architectural
         heritage as well as elite 19th century architecture. In 1938 the theme park Marineland opened just south
         of St. Augustine, becoming one of Florida's first themed parks and setting the stage for the development
         of this industry in the following decades. The city is also one terminus of the Old Spanish Trail, which
         in the 1920's linked St. Augustine, Florida, to San Diego, California with 3000 miles of roadways.</p>
   </div>

</portal:page>

<%@ include file="/layouts/footer.jsp" %>