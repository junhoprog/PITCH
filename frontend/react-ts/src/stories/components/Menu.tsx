import styled, { keyframes } from 'styled-components';
import { colors } from '../../styles/color';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import useStore from '../store/store';

const slideIn = keyframes`
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
`;

const slideOut = keyframes`
  from {
    transform: translateX(0);
    opacity: 1;
    visibility: 1;
  }
  to {
    transform: translateX(100%);
    opacity: 0;
    visibility: 0;
  }
`;

const MenuArea = styled.div<{ isExiting: boolean }>`
  position: fixed; // 또는 absolute
  z-index: 11;
  width: 100vw;
  height: 100vh;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-end;
  padding-right: 20px;

  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);

  animation: ${props => (props.isExiting ? slideOut : slideIn)} 0.2s forwards;
`;

const MenuCloseButton = styled.div`
  display: flex;
  margin-right: 15px;
`;

const ButtonArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-end;
  gap: 20px;
  margin-right: 20px;
`;

const Button = styled.div`
  width: auto;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  flex-direction: row;
`;

const Title = styled.div<{ fontSize: string; margin: string; color: string; opacity: number }>`
  font-size: ${props => props.fontSize};
  margin: ${props => props.margin};
  color: ${props => props.color};
  font-family: 'Bd';
  white-space: nowrap;
  opacity: ${props => props.opacity};
`;

const LogOutArea = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  gap: 5px;
  margin: 80px 20px 0px 0px;
`;

interface MenuProps {
  // onClick?: () => void;
  page: number;
  isMenuOpen: boolean;
  setIsMenuOpen: (isMenuOpen: boolean) => void;
}

function Menu({ page, isMenuOpen, setIsMenuOpen }: MenuProps) {
  console.log(page);
  const navigate = useNavigate();
  const { id } = useStore();

  const GoToHomePage = () => {
    navigate('/Home');
  };
  const GoToMusicProfilePage = () => {
    navigate('/MusicProfilePage', { state: id });
  };
  const GoToAlbumHomePage = () => {
    navigate('/AlbumHomePage');
  };
  const GoToStartPage = () => {
    navigate('/');
  };

  const LogOut = () => {
    localStorage.removeItem('login-token');
    localStorage.removeItem('login-refreshToken');
    localStorage.removeItem('user-info');
    GoToStartPage();
  };
  const [activeNav, setActiveNav] = useState(page);
  return (
    <MenuArea isExiting={!isMenuOpen}>
      <MenuCloseButton
        onClick={() => {
          setIsMenuOpen(!isMenuOpen);
        }}
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="50" height="50" fill="currentColor" className="bi bi-x" viewBox="0 0 16 16">
          <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708" />
        </svg>
      </MenuCloseButton>
      <ButtonArea>
        <Button
          onClick={() => {
            setActiveNav(1);
            GoToHomePage();
          }}
        >
          <svg
            color={colors.Main_Pink}
            opacity={activeNav === 1 ? 1 : 0.5}
            width={activeNav === 1 ? '40px' : '25px'}
            height={activeNav === 1 ? '40px' : '25px'}
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            className="bi bi-house-door-fill"
            viewBox="0 0 16 16"
          >
            <path d="M6.5 14.5v-3.505c0-.245.25-.495.5-.495h2c.25 0 .5.25.5.5v3.5a.5.5 0 0 0 .5.5h4a.5.5 0 0 0 .5-.5v-7a.5.5 0 0 0-.146-.354L13 5.793V2.5a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1.293L8.354 1.146a.5.5 0 0 0-.708 0l-6 6A.5.5 0 0 0 1.5 7.5v7a.5.5 0 0 0 .5.5h4a.5.5 0 0 0 .5-.5" />
          </svg>
          <Title color={colors.Main_Pink} opacity={activeNav === 1 ? 1 : 0.5} fontSize={activeNav === 1 ? '35px' : '20px'} margin="0px 5px">
            Home
          </Title>
        </Button>
        <Button
          onClick={() => {
            setActiveNav(2);
            GoToMusicProfilePage();
          }}
        >
          <svg
            color={colors.Main_Pink}
            opacity={activeNav === 2 ? 1 : 0.5}
            width={activeNav === 2 ? '40px' : '25px'}
            height={activeNav === 2 ? '40px' : '25px'}
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            className="bi bi-person-fill"
            viewBox="0 0 16 16"
          >
            <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6" />
          </svg>
          <Title color={colors.Main_Pink} opacity={activeNav === 2 ? 1 : 0.5} fontSize={activeNav === 2 ? '35px' : '20px'} margin="0px 5px">
            Music Profile
          </Title>
        </Button>
        <Button
          onClick={() => {
            setActiveNav(2);
            GoToAlbumHomePage();
          }}
        >
          <svg
            color={colors.Main_Pink}
            opacity={activeNav === 3 ? 1 : 0.5}
            width={activeNav === 3 ? '40px' : '25px'}
            height={activeNav === 3 ? '40px' : '25px'}
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            className="bi bi-union"
            viewBox="0 0 16 16"
          >
            <path d="M0 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v2h2a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-2H2a2 2 0 0 1-2-2z" />
          </svg>
          <Title color={colors.Main_Pink} opacity={activeNav === 3 ? 1 : 0.5} fontSize={activeNav === 3 ? '35px' : '20px'} margin="0px 5px">
            Albums
          </Title>
        </Button>
      </ButtonArea>
      <LogOutArea
        onClick={() => {
          LogOut();
        }}
      >
        <svg color={colors.Font_grey} xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-box-arrow-right" viewBox="0 0 16 16">
          <path
            fill-rule="evenodd"
            d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0z"
          />
          <path fill-rule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708z" />
        </svg>
        <Title color={colors.Font_grey} opacity={1} fontSize="15px" margin="0px">
          Log Out
        </Title>
      </LogOutArea>
    </MenuArea>
  );
}

export default Menu;