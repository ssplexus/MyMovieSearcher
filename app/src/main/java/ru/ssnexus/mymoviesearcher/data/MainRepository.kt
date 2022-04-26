package ru.ssnexus.mymoviesearcher.data

import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.domain.Film

class MainRepository {
    val filmsDataBase = listOf(
        Film(0,"Black Phone",
            R.drawable.black_phone, "After being abducted by a child killer and locked in a soundproof basement, a 13-year-old boy starts receiving calls on a disconnected phone from the killer's previous victims.", 7.1f),
        Film(1,"Hannibal",
            R.drawable.hannibal,"Living in exile, Dr. Hannibal Lecter tries to reconnect with now disgraced F.B.I. Agent Clarice Starling, and finds himself a target for revenge from a powerful victim.", 9.8f),
        Film(2,"Horse Whisperer",
            R.drawable.horse_whisperer,"The mother of a severely traumatized daughter enlists the aid of a unique horse trainer to help the girl's equally injured horse.", 9.1f),
        Film(3,"Hostel",
            R.drawable.hostel,"Three backpackers head to a Slovak city that promises to meet their hedonistic expectations, with no idea of the hell that awaits them.", 5.4f),
        Film(4,"Lost City",
            R.drawable.lost_city, "A reclusive romance novelist on a book tour with her cover model gets swept up in a kidnapping attempt that lands them both in a cutthroat jungle adventure.", 6.3f),
        Film(5,"Saw",
            R.drawable.sawx,"As a deadly battle rages over Jigsaw's brutal legacy, a group of Jigsaw survivors gathers to seek the support of self-help guru and fellow survivor Bobby Dagen, a man whose own dark secrets unleash a new wave of terror.", 7.5f),
        Film(6,"When Will I Be Loved",
            R.drawable.when_will_i_be_loved, "Feeling undervalued by her boyfriend, a young woman begins to explore her sexuality with other people.", 8.3f)
    )
}
