from bd import *
from entities import bd
import re

pattern = re.compile(r'^%.*$', re.MULTILINE)
bd = re.sub(pattern, '', bd)
bd = re.sub(r'\n+', '\n', bd).strip()
bd = bd.split(".\n")

compounds = [i.replace("organic_compound('", "").replace("').", "") for i in bd if bool(re.match(r'^organic_compound\([^)]*\)$', i))]
has_class = [i.replace("chemical_class('", "").replace(")", "").split("', ") for i in bd if
               bool(re.match(r'^chemical_class\([^)]*\)$', i))]
has_molar_mass = [i.replace("molar_mass('", "").replace(")", "").split("', ") for i in bd if
             bool(re.match(r'^molar_mass\([^)]*\)$', i))]
has_density = [i.replace("density('", "").replace(")", "").split("', ") for i in bd if
             bool(re.match(r'^density\([^)]*\)$', i))]
has_temps = [i.replace("temperatures('", "").replace(")", "").split("', ") for i in bd if
             bool(re.match(r'^temperatures\([^)]*\)$', i))]
has_vapor_pressure = [i.replace("vapor_pressure('", "").replace(")", "").split("', ") for i in bd if
             bool(re.match(r'^vapor_pressure\([^)]*\)$', i))]
has_water_solubility = [i.replace("water_solubility('", "").replace(")", "").split("', ") for i in bd if
             bool(re.match(r'^water_solubility\([^)]*\)$', i))]
has_nfpa = [i.replace("nfpa('", "").replace(")", "").split("', ") for i in bd if
             bool(re.match(r'^nfpa\([^)]*\)$', i))]

for compound in compounds:
    info = f"""\t<owl:NamedIndividual rdf:about="#{compound.replace(" ", "_").replace(":","")}">\n\t\t<rdf:type rdf:resource="#compound"/>"""
    for [organic_compound, nomenclature] in has_class:
        if organic_compound == compound:
            info += f"""\n\t\t<hasClass rdf:resource="#{nomenclature}"/>"""
    for [organic_compound, molar_mass] in has_molar_mass:
        if organic_compound == compound:
            info += f"""\n\t\t<hasMolarMass rdf:resource="#{molar_mass}"/>"""
    for [organic_compound, density] in has_density:
        if organic_compound == compound:
            info += f"""\n\t\t<hasDensity rdf:resource="#{density}"/>"""
    for [organic_compound, vapor] in has_vapor_pressure:
        if organic_compound == compound:
            info += f"""\n\t\t<hasVaporPressure rdf:resource="#{vapor}"/>"""
    for [organic_compound, sol] in has_water_solubility:
        if organic_compound == compound:
            info += f"""\n\t\t<hasWaterSolubility rdf:resource="#{sol}"/>"""
    info += "\n\t</owl:NamedIndividual>"
    print(info)
