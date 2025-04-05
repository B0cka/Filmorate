package FilmDBStorageTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FilmDbStorage.class)
public class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Test
    public void testCreateAndFindFilmById() {

        Film film = new Film();
        film.setDescription("bldldldldldl");
        film.setDuration(100);
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);
        Film created = filmDbStorage.create(film);

        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(created.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getId()).isEqualTo(created.getId());
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getDescription()).isEqualTo("bldldldldldl");
                    assertThat(f.getDuration()).isEqualTo(100);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }

    @Test
    public void testUpdateAndFindFilmById() {

        Film film = new Film();
        film.setDescription("bldldldldldl");
        film.setDuration(100);
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);
        Film created = filmDbStorage.create(film);

        film.setDescription("bldldldldldl1232211");
        Film updated =  filmDbStorage.update(film);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getById(updated.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getId()).isEqualTo(updated.getId());
                    assertThat(f.getName()).isEqualTo("Test Film");
                    assertThat(f.getDescription()).isEqualTo("bldldldldldl1232211");
                    assertThat(f.getDuration()).isEqualTo(100);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }


    @Test
    public void testFindFilmById() {
        Film film = new Film();
        film.setName("Find Me");
        film.setDescription("Find me by ID");
        film.setDuration(110);
        film.setReleaseDate(LocalDate.of(2010, 4, 20));
        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        film.setMpa(mpa);

        Film created = filmDbStorage.create(film);

        Optional<Film> found = Optional.ofNullable(filmDbStorage.getById(created.getId()));
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Find Me");
                    assertThat(f.getDescription()).isEqualTo("Find me by ID");
                });
    }


}
